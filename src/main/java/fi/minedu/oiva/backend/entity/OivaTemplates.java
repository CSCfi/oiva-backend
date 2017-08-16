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

    public enum Attachment {
        tutkintoNimenMuutos
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

        public RenderOptions addAttachment(final Attachment attachment) {
            this.attachments.add(attachment);
            return this;
        }

        public boolean hasAttachment(final Attachment targetAttachment) {
            return this.attachments.stream().anyMatch(attachment -> attachment == targetAttachment);
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
