package fi.minedu.oiva.backend.core.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;
import static fi.minedu.oiva.backend.model.entity.OivaTemplates.AttachmentType;
import static fi.minedu.oiva.backend.model.entity.OivaTemplates.RenderLanguage;
import static fi.minedu.oiva.backend.model.entity.OivaTemplates.RenderOptions;

@Service
public class PrinceXMLService {

    private static final Logger logger = LoggerFactory.getLogger(PrinceXMLService.class);

    @Value("${templates.base.path}")
    private String templateBasePath;

    @Value("${prince.exec.path}")
    private String princeExecPath;

    public boolean toPDF(final String html, final OutputStream output, final RenderOptions options)  {
        final ByteArrayOutputStream basePDFStream = new ByteArrayOutputStream();
        boolean succesfull = generatePDF(html, basePDFStream);
        if(succesfull) {
            return appendAttachments(new ByteArrayInputStream(basePDFStream.toByteArray()), output, options);
        }
        return false;
    }

    protected boolean appendAttachments(final InputStream base, final OutputStream output, final RenderOptions options) {

        try {
            final PDFMergerUtility merger = new PDFMergerUtility();

            // 4.10.2018 tulleet korjaukset
            if(options.hasAttachment(AttachmentType.korjaukset2018)) {
                merger.addSource(templateBasePath + "/liitteet/" + options.getAttachment(AttachmentType.korjaukset2018).getPath());
            }

            // 21.12.2017 tulleet hallintolakiin liittyvät korjaukset
            if(options.hasAttachment(AttachmentType.hallintolakiKorjaus2017v2)) {
                merger.addSource(templateBasePath + "/liitteet/" + options.getAttachment(AttachmentType.hallintolakiKorjaus2017v2).getPath());
            }

            // 12.12.2017 tulleet hallintolakiin liittyvät korjaukset
            if(options.hasAttachment(AttachmentType.hallintolakiKorjaus2017)) {
                merger.addSource(templateBasePath + "/liitteet/" + options.getAttachment(AttachmentType.hallintolakiKorjaus2017).getPath());
            }

            if(options.hasAttachment(AttachmentType.paatosKirje)) {
                merger.addSource(templateBasePath + "/liitteet/" + options.getAttachment(AttachmentType.paatosKirje).getPath());
            }

            merger.addSource(base);

            if(options.hasAttachment(AttachmentType.tutkintoNimenMuutos)) {
                if(options.isLanguage(RenderLanguage.sv)) {
                    merger.addSource(templateBasePath + "/liitteet/LIITE-tutkintojen_nimien_muutokset_sv.pdf");
                } else {
                    merger.addSource(templateBasePath + "/liitteet/LIITE-tutkintojen_nimien_muutokset.pdf");
                }
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
        else if (!new File(princeExecPath).exists()) {
            logger.error("PrinceXML binary executable not found in " + princeExecPath);
            return false;
        }

        String errorMsg = "Error using PrinceXML to generate PDF file. Make sure PrinceXML could be run manually: {} --version";
        try {
            boolean result = convertXmlToPdf(IOUtils.toInputStream(html), output);
            if (result) {
                return true;
            }
            else {
                logger.error(errorMsg, princeExecPath);
            }
        } catch (IOException e) {
            logger.error(errorMsg, princeExecPath, e);
        }

        return false;

    }

    protected boolean healthCheck()  {
        return generatePDF("<html lang=\"fi\"><title>Health check</title></html>", NullOutputStream.NULL_OUTPUT_STREAM);
    }

    // Implementation copied and slightly modified from "official" library (methods convert and readMessages)
    // https://github.com/yeslogic/prince-tools/blob/master/wrappers/java/Prince.java
    public boolean convertXmlToPdf(InputStream xmlInput, OutputStream pdfOutput) throws IOException {
        String[] cmdline = new String[]{
                princeExecPath,
                "--structured-log=buffered",
                "--pdf-profile=PDF/UA-1",
                "--verbose",
                "-"
        };

        logger.debug("Running command: {}", StringUtils.join(cmdline, " "));
        Process process = Runtime.getRuntime().exec(cmdline);
        OutputStream inputToPrince = process.getOutputStream();
        InputStream outputFromPrince = process.getInputStream();

        // copy the XML input to Prince stdin
        IOUtils.copy(xmlInput, inputToPrince);
        inputToPrince.close();

        // copy the PDF output from Prince stdout
        IOUtils.copy(outputFromPrince, pdfOutput);
        outputFromPrince.close();

        // All logs are printed to stderr
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        AtomicReference<String> lastLine = new AtomicReference<>("");
        reader.lines().forEach(l -> {
            if (l.contains("error:")) {
                logger.error("PrinceXML: {}", l);
            }
            else if (l.contains("|err|")) {
                logger.warn("PrinceXML: {}", l);
            }
            else {
                logger.debug("PrinceXML: {}", l);
            }
            lastLine.set(l);
        });

        return lastLine.get().contains("success");
    }
}
