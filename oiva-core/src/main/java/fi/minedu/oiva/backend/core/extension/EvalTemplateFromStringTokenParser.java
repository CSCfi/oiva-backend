package fi.minedu.oiva.backend.core.extension;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Optional;

public class EvalTemplateFromStringTokenParser extends AbstractTokenParser {

    @Override
    public RenderableNode parse(final Token token, final Parser parser) throws ParserException {
        final TokenStream stream = parser.getStream();
        int lineNumber = token.getLineNumber();
        stream.next();

        final Expression<?> includeExpression = parser.getExpressionParser().parseExpression();
        stream.expect(Token.Type.EXECUTE_END);

        return new IncludeAsStringNode(lineNumber, includeExpression);
    }

    @Override
    public String getTag() {
        return "eval";
    }

    public static class IncludeAsStringNode extends AbstractRenderableNode {
        private final Expression<?> includeExpression;
        public IncludeAsStringNode(int lineNumber, Expression<?> includeExpression) {
            super(lineNumber);
            this.includeExpression = includeExpression;
        }

        @Override
        public void render(final PebbleTemplateImpl self, final Writer writer, final EvaluationContext context) throws PebbleException, IOException {
            final String template = (String) includeExpression.evaluate(self, context);
            if (null == template) {
                throw new PebbleException(null,
                    "The template in an include tag evaluated to NULL",
                    getLineNumber(), self.getName());
            }
            final Optional<PebbleTemplateImpl> opt = context.getImportedTemplates().stream().filter(t -> StringUtils.equals(t.getName(), template)).findAny();
            if(opt.isPresent()) {
                self.includeTemplate(writer, context, opt.get().getName(), Collections.emptyMap());
            }
        }

        @Override
        public void accept(NodeVisitor visitor) {
            visitor.visit(this);
        }

        public Expression<?> getIncludeExpression() {
            return includeExpression;
        }
    }
}
