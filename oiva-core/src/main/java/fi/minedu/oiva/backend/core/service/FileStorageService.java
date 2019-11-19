package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.core.config.FileStorageConfig;
import fi.minedu.oiva.backend.model.entity.OivaTemplates;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.core.util.ExecutorContext;
import fi.minedu.oiva.backend.core.util.With;
import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private static final String writeAllPDFs = "pdfwriter";

    private final FileStorageConfig fileStorage;

    private final LupaService lupaService;

    private final LupaRenderService lupaRenderService;

    private final BasePebbleService pebbleService;

    private final PrinceXMLService princeXMLService;

    private final AsyncService asyncService;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorage, LupaService lupaService,
                              LupaRenderService lupaRenderService, BasePebbleService pebbleService,
                              PrinceXMLService princeXMLService, AsyncService asyncService) {
        this.fileStorage = fileStorage;
        this.lupaService = lupaService;
        this.lupaRenderService = lupaRenderService;
        this.pebbleService = pebbleService;
        this.princeXMLService = princeXMLService;
        this.asyncService = asyncService;
    }
    public void writeHakemusPDF(final Muutospyynto muutospyynto) throws Exception {
        if(muutospyynto == null) {
            throw new NullPointerException("Muutospyynto must not be null");
        }
        final OivaTemplates.RenderOptions options = OivaTemplates.RenderOptions.pdfOptions(OivaTemplates.RenderLanguage.fi);
        final String muutospyyntoHTMLVersion = pebbleService.muutospyyntoToHTML(muutospyynto, options).orElseThrow(IllegalStateException::new);
        final File file = createHakemusFile(muutospyynto).orElseThrow(IllegalStateException::new);
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        boolean writeResult = princeXMLService.toPDF(muutospyyntoHTMLVersion, fileOutputStream, options);
        if(!writeResult) {
            throw new Exception("Unable to write PDF content");
        }
    }

    public Optional<File> writeLupaPDF(final Lupa lupa) throws Exception {
        final Optional<Lupa> lupaOpt = Optional.ofNullable(lupa);
        if (lupaOpt.isPresent()) {
            final OivaTemplates.RenderOptions renderOptions = lupaRenderService.getLupaRenderOptions(lupaOpt).orElseThrow(IllegalStateException::new);
            final String lupaHtml = pebbleService.toHTML(lupaOpt.get(), renderOptions).orElseThrow(IllegalStateException::new);
            final File file = createLupaFile(lupaOpt.get()).orElseThrow(IllegalStateException::new);
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            if (princeXMLService.toPDF(lupaHtml, fileOutputStream, renderOptions)) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    private File createFileWithPath(String filePath) {
        final File file = new File(filePath);
        final File folder = file.getParentFile();
        if (folder.exists() || folder.mkdirs()) {
            return file;
        }
        logger.error("Failed to create file " + filePath);
        return null;
    }

    private Optional<File> createHakemusFile(final Muutospyynto muutospyynto) {
        Optional<Muutospyynto> muutospyyntoOpt = Optional.ofNullable(muutospyynto);

        return muutospyyntoOpt.map(mp ->
                getHakemusPDFFilePath(mp).map(filePath ->
                        createFileWithPath(filePath)
                ).orElse(null));
    }

    private Optional<File> createLupaFile(final Lupa lupa) {
        Optional<Lupa> lupaOpt = Optional.ofNullable(lupa);
        return lupaOpt.map(l ->
                getLupaPDFFilePath(l).map(filePath ->
                        createFileWithPath(filePath)
                ).orElse(null));
    }

    public Optional<String> getLupaPDFFilePath(final Lupa lupa) {
        Optional<Lupa> lupaOpt = Optional.ofNullable(lupa);
        if (lupaOpt.isPresent()) {
            final Lupa l = lupaOpt.get();
            return Optional.of(fileStorage.getLuvatBasePath() + "/" + l.getUUIDValue() + "/" + l.getPDFFileName());
        } else return Optional.empty();
    }

    public Optional<String> getHakemusPDFFilePath(final Muutospyynto muutospyynto) {
        Optional<Muutospyynto> muutospyyntoOpt = Optional.ofNullable(muutospyynto);
        if(muutospyyntoOpt.isPresent()) {
            final Muutospyynto mp = muutospyyntoOpt.get();
            return Optional.of(fileStorage.getHakemuksetBasePath() + "/" + mp.getUuid().toString() + ".pdf");
        } else return Optional.empty();
    }

    /**
     * Support service
     */
    public Collection<String> writeAllLupaPDFs(final String operation) {
        final String executorName = "Executor " + writeAllPDFs;
        final Optional<ExecutorContext> existingExecutorContext = asyncService.getContext(writeAllPDFs);
        if (StringUtils.equalsIgnoreCase(operation, "terminate")) {
            if (existingExecutorContext.isPresent()) {
                asyncService.terminate(writeAllPDFs);
                return Collections.singleton(executorName + " termination requested");
            }
            return Collections.singleton(executorName + "does not exist");
        }

        final Function<Lupa, Optional<Lupa>> toLupa = lupa -> lupaService.getByYtunnus(lupa.getJarjestajaYtunnus(), With.all);
        final Consumer<String> addExecutorState = state -> asyncService.addState(writeAllPDFs, state);
        if (existingExecutorContext.isPresent()) {
            return existingExecutorContext.get().reversedStates();
        } else if (StringUtils.equalsIgnoreCase(operation, "start")) {
            logger.info("Starting to write all Lupa PDFs, it will take awhile");
            final ExecutorContext executorContext = asyncService.create(writeAllPDFs, executorName + "process started").execute(() -> {
                final long startTime = System.currentTimeMillis();
                lupaService.getAll().stream().parallel().map(toLupa).forEach(lupaOpt -> {
                    lupaOpt.ifPresent(l -> {
                        final String diaariNumero = l.getDiaarinumero();
                        final String statePrefix = "Lupa " + diaariNumero;
                        try {
                            final Optional<File> writtenFile = writeLupaPDF(lupaOpt.get());
                            if (writtenFile.isPresent()) {
                                logger.info("Lupa {} PDF saved", diaariNumero);
                                addExecutorState.accept(statePrefix + " --> Success: " + writtenFile.get().getAbsolutePath());
                            } else {
                                logger.error("Failed to save lupa {} PDF", diaariNumero);
                                addExecutorState.accept(statePrefix + " --> Failed");
                            }
                        } catch (Exception e) {
                            logger.error("Failed to save lupa (id=" + lupaOpt.get().getId() + ") PDF", e);
                            addExecutorState.accept(statePrefix + " --> Failed");
                        }
                    });
                    final long duration = System.currentTimeMillis() - startTime;
                    addExecutorState.accept(executorName + " process finished in " + duration + "ms");
                    logger.info("Lupa PDF writing finished in {}ms", duration);
                });
            });
            return executorContext.reversedStates();
        }
        return Collections.singleton(executorName + " does not exist");
    }
}
