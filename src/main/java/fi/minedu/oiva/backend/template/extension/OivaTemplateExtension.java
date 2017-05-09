package fi.minedu.oiva.backend.template.extension;

import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.minedu.oiva.backend.entity.MaaraystyyppiValue.*;
import static fi.minedu.oiva.backend.template.extension.AppendFilter.Type.prefix;
import static fi.minedu.oiva.backend.template.extension.MaaraysListFilter.Method.*;
import static fi.minedu.oiva.backend.template.extension.SortListFilter.SortTarget.*;

public class OivaTemplateExtension implements Extension {

    @Override
    public Map<String, Filter> getFilters() {
        Map<String,Filter> filters = new HashMap<>();
        filters.put("translated", new TranslateFilter());
        filters.put("field", new JsonFieldFilter(JsonFieldFilter.ReturnType.JsonNode, false));
        filters.put("fieldvalue", new JsonFieldFilter(JsonFieldFilter.ReturnType.String, false));
        filters.put("fieldvalue_translated", new JsonFieldFilter(JsonFieldFilter.ReturnType.String, true));
        filters.put("comma", new SeparatorFilter(", "));
        filters.put("semicolon", new SeparatorFilter("; "));
        filters.put("withColonPrefix", new AppendFilter(prefix, ": "));
        filters.put("sortByLanguage", new SortByLanguageFilter()); // TODO: REMOVE ME
        filters.put("toDate", new ToDateFilter());
        filters.put("maaraysFilter", new MaaraysListFilter(combo));
        filters.put("sortLuvat", new SortListFilter(Luvat));
        filters.put("sortMaaraykset", new SortListFilter(Maaraykset));

        // TODO: ADD SORTER FILTERS

        filters.put("kohde", new MaaraysListFilter(byKohdeTunniste)); // TODO: REPLACE ME WITH maaraysFilter
        filters.put("koodisto", new MaaraysListFilter(byKoodistoUri)); // TODO: REPLACE ME WITH maaraysFilter
        filters.put("sallittuKohde", new MaaraysListFilter(byKohdeTunniste, VELVOITE, OIKEUS)); // TODO: REPLACE ME WITH maaraysFilter
        filters.put("velvoiteKohde", new MaaraysListFilter(byKohdeTunniste, VELVOITE)); // TODO: REPLACE ME WITH maaraysFilter
        filters.put("oikeusKohde", new MaaraysListFilter(byKohdeTunniste, OIKEUS)); // TODO: REPLACE ME WITH maaraysFilter
        filters.put("rajoiteKohde", new MaaraysListFilter(byKohdeTunniste, RAJOITE)); // TODO: REPLACE ME WITH maaraysFilter

        return filters;
    }

    @Override
    public Map<String, Test> getTests() {
        Map<String,Test> testMap = new HashMap<>();
        testMap.put("hasTranslation", new HasTranslationTest()); // TODO: REMOVE ME
        testMap.put("notBlank", new NotEmptyTest());
        testMap.put("dateGT", new DateComparatorTest(">"));
        testMap.put("dateGTE", new DateComparatorTest(">="));
        testMap.put("dateLT", new DateComparatorTest("<"));
        testMap.put("dateLTE", new DateComparatorTest("<="));
        testMap.put("dateEQ", new DateComparatorTest("="));
        testMap.put("dateBetween", new DateBetweenTest(">=", "<="));
        return testMap;
    }

    @Override
    public Map<String, Function> getFunctions() {
        Map<String,Function> funMap = new HashMap<>();
        funMap.put("fromRoot", new TemplateFinderFunction(null));
        funMap.put("fromDefault", new TemplateFinderFunction("defaultPath"));
        funMap.put("fromContext", new TemplateFinderFunction("contextPath"));
        return funMap;
    }

    @Override
    public List<TokenParser> getTokenParsers() {
        return Collections.singletonList(new EvalTemplateFromStringTokenParser());
    }

    @Override
    public List<BinaryOperator> getBinaryOperators() {
        return null;
    }

    @Override
    public List<UnaryOperator> getUnaryOperators() {
        return null;
    }

    @Override
    public Map<String, Object> getGlobalVariables() {
        return null;
    }

    @Override
    public List<NodeVisitorFactory> getNodeVisitors() {
        return null;
    }
}
