package fi.minedu.oiva.backend.entity;

import java.util.ArrayList;
import java.util.Collection;

public class OivaTemplates {

    public enum RenderState {
        draft, finished
    }

    public enum RenderLanguage {
        fi, sv, en, se
    }

    public enum RenderOutput {
        web, pdf
    }

    public enum AttachmentType {
        tutkintoNimenMuutos,
        paatosKirje;

        public static AttachmentType convert(String str) {
            for (AttachmentType attachmentType : AttachmentType.values()) {
                if (attachmentType.toString().equals(str)) {
                    return attachmentType;
                }
            }
            return null;
        }
    }

    public static class Attachment {
        private AttachmentType type;
        private String path;
        private String language;

        public String getPath() { return this.path; }
        public void setPath(String path) { this.path = path; }

        public AttachmentType getType() { return this.type; }
        public void setType(AttachmentType type) { this.type = type; }
    }

    public static class RenderOptions {
        private RenderOutput output;
        private RenderState state;
        private RenderLanguage language;
        private String templateName;
        private Collection<Attachment> attachments;
        private boolean debugMode;

        public static RenderOptions webOptions(final RenderLanguage language) {
            return new RenderOptions(RenderOutput.web, RenderState.draft, language);
        }

        public static RenderOptions pdfOptions(final RenderLanguage language) {
            return new RenderOptions(RenderOutput.pdf, RenderState.draft, language);
        }

        public RenderOptions(final RenderOutput output, final RenderState state, final RenderLanguage language) {
            this.output = output;
            this.state = state;
            this.language = language;
            this.attachments = new ArrayList();
            this.debugMode = false;
        }

        public boolean isType(final RenderOutput output) {
            return this.output == output;
        }

        public RenderOptions setState(final RenderState state) {
            this.state = state;
            return this;
        }

        public RenderOptions setLanguage(final RenderLanguage language) {
            this.language = language;
            return this;
        }

        public boolean isLanguage(final RenderLanguage language) {
            return this.language == language;
        }

        public RenderOutput getOutput() {
            return output;
        }

        public RenderState getState() {
            return state;
        }

        public RenderLanguage getLanguage() {
            return language;
        }

        public String getTemplateName() {
            return templateName;
        }

        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        public RenderOptions addAttachment(final AttachmentType attachmentType, final String path) {
            final Attachment attachment = new Attachment();
            attachment.setPath(path);
            attachment.setType(attachmentType);
            this.attachments.add(attachment);
            return this;
        }

        public Attachment getAttachment(final AttachmentType attachmentType) {
            return this.attachments.stream().filter(p -> p.type.equals(attachmentType)).findFirst().get();
        }

        public boolean hasAttachment(final AttachmentType targetAttachment) {
            return this.attachments.stream().anyMatch(attachment -> attachment.type == targetAttachment);
        }

        public RenderOptions setDebugMode(final boolean debug) {
            this.debugMode = debug;
            return this;
        }

        public boolean isDebugMode() {
            return this.debugMode;
        }
    }
}
