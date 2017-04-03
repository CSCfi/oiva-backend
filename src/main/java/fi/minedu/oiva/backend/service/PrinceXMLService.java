package fi.minedu.oiva.backend.service;

import com.princexml.Prince;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static fi.minedu.oiva.backend.entity.OivaTemplates.RenderLanguage;

@Service
public class PrinceXMLService {

    private static final Logger logger = LoggerFactory.getLogger(PrinceXMLService.class);

    @Value("${templates.base.path}")
    private String templateBasePath;

    @Value("${prince.exec.path}")
    private String princeExecPath;

    public boolean generatePDF(final String html, final OutputStream output)  {
        if (null == princeExecPath || "${prince.exec.path}".equals(princeExecPath)) {
            logger.error("prince.exec.path not defined");
            return false;
        }

        final Prince prince = new Prince(princeExecPath, (msg1, msg2, msg3) -> {
            logger.info("Prince data " + msg1 + " -- " + msg2 + " --- " + msg3);
        });

        try {
            return prince.convert(IOUtils.toInputStream(html), output);

        } catch (IOException e) {
            logger.error("Failed to generate PDF from html");
            return false;
        }
    }

    public void generateMergedPDF(final String html, final OutputStream output, final RenderLanguage language) {

        final File basePDF = toFile(html);
        try {

            final PDFMergerUtility merger = new PDFMergerUtility();
            merger.addSource(basePDF);

            final String pdfPath = valiPDF(null, language); // TODO add hallintoOikeus support
            merger.addSource(pdfPath);

            if (false) { // TODO add logic
                merger.addSource(oikoPDF(language));
            }

            merger.setDestinationStream(output);
            merger.mergeDocuments();

        } catch (Exception e) {
            logger.error("Failed to generate merged PDF", e);
            throw new RuntimeException("Failed to generate merged PDF", e);

        } finally {
            if (null != basePDF) {
                basePDF.delete();
            }
        }
    }

    private File toFile(final String html) {
        try {
            final Path tmpFile = Files.createTempFile(null, "_oiva");
            tmpFile.toFile().deleteOnExit();
            generatePDF(html, Files.newOutputStream(tmpFile));
            return tmpFile.toFile();

        } catch (Exception e) {
            logger.error("Failure writing file", e);
            throw new RuntimeException("Failure writing file", e);
        }
    }

    private String oikoPDF(final RenderLanguage language) throws IOException {
        return templateBasePath + "/valitus/oiko_" + language.name() + ".PDF";
    }

    // TODO: add paatoskierros path
    private String valiPDF(final String hallintoOikeus, final RenderLanguage language) throws IOException {
        final String basePath = templateBasePath + "/valitus/";
        if (StringUtils.isNotBlank(hallintoOikeus)) {
            return basePath+"vali2016_"+hallintoOikeus+"_"+language.name()+".PDF";

        } else {
            return basePath+"vali_"+language.name()+".PDF";
        }
    }
}
