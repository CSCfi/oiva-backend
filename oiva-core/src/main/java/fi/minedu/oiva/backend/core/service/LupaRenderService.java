package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.OivaTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static fi.minedu.oiva.backend.model.entity.OivaTemplates.RenderOptions;

@Service
public class LupaRenderService {

    private static final Logger logger = LoggerFactory.getLogger(LupaRenderService.class);

    @Autowired
    private LupaService lupaService;

    public Optional<RenderOptions> getLupaRenderOptions(final Optional<Lupa> lupaOpt) {
        if(lupaOpt.isPresent()) {
            try {
                final Lupa lupa = lupaOpt.get();
                final RenderOptions renderOptions = RenderOptions.pdfOptions(lupaService.renderLanguageFor(lupa));
                if (lupa.getId() != null) {
                    lupaService.getAttachments(lupa.getId()).forEach(attachment ->
                            Optional.ofNullable(OivaTemplates.AttachmentType.convert(attachment.getTyyppi())).ifPresent(attachmentType ->
                                    renderOptions.addAttachment(attachmentType, attachment.getPolku())
                            )
                    );
                }
                // Tulostetaan nimenmuutosliite, mikäli luvassa on tutkintoja joiden nimi muuttuu.
                // Lisäksi päätöksen tulee olla tehty ennen lokakuuta 2018.
                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                final Date attachmentLastDate = dateFormat.parse("01/10/2018");
                if (lupaService.hasTutkintoNimenMuutos(lupa) && lupa.getPaatospvm() != null && lupa.getPaatospvm().before(attachmentLastDate)) {
                    renderOptions.addAttachment(OivaTemplates.AttachmentType.tutkintoNimenMuutos, "LIITE-tutkintojen_nimien_muutokset.pdf");
                }
                return Optional.of(renderOptions);
            } catch (Exception e) {
                logger.error("Failed to prepare default render options", e);
            }
        }
        return Optional.empty();
    }
}