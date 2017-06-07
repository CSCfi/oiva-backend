package fi.minedu.oiva.backend.service;

import com.princexml.Prince;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static fi.minedu.oiva.backend.entity.OivaTemplates.*;

@Service
public class PrinceXMLService {

    private static final Logger logger = LoggerFactory.getLogger(PrinceXMLService.class);

    @Value("${templates.base.path}")
    private String templateBasePath;

    @Value("${prince.exec.path}")
    private String princeExecPath;

    public boolean toPDF(final String html, final OutputStream output, final RenderOptions options)  {
        final ByteArrayOutputStream basePDFStream = new ByteArrayOutputStream();
        if(generatePDF(html, basePDFStream)) {
            return appendAttachments(new ByteArrayInputStream(basePDFStream.toByteArray()), output, options);
        }
        return false;
    }

    protected boolean appendAttachments(final InputStream base, final OutputStream output, final RenderOptions options) {

        try {
            final PDFMergerUtility merger = new PDFMergerUtility();
            merger.addSource(base);

            if(options.hasAttachment(Attachment.tutkintoNimenMuutos)) {
                merger.addSource(templateBasePath + "/liitteet/LIITE-tutkintojen_nimien_muutokset.pdf");
            }

            merger.setDestinationStream(output);
            merger.mergeDocuments();

        } catch (Exception e) {
            logger.error("Failed to merge attachments to PDF", e);
            return false;
        }
        return true;
    }

    protected boolean generatePDF(final String html, final OutputStream output)  {
        if (null == princeExecPath || "${prince.exec.path}".equals(princeExecPath)) {
            logger.error("prince.exec.path not defined");
            return false;
        }

        final Prince prince = new Prince(princeExecPath, (msg1, msg2, msg3) -> logger.info("Prince data " + msg1 + " -- " + msg2 + " --- " + msg3));

        try {
            return prince.convert(IOUtils.toInputStream(html), output);
        } catch (IOException e) {
            logger.error("Failed to generate PDF from html");
            return false;
        }
    }
}
