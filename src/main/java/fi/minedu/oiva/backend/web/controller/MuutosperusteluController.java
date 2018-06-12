package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.Muutospyynto;
import fi.minedu.oiva.backend.jooq.tables.pojos.Muutos;

import fi.minedu.oiva.backend.security.annotations.OivaAccess_Kayttaja;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.MuutospyyntoService;
import fi.minedu.oiva.backend.util.RequestUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(
        value = "${api.url.prefix}" + MuutosperusteluController.path,
        produces = { MediaType.APPLICATION_JSON_VALUE })
public class MuutosperusteluController {

    @Value("${templates.base.path}")
    private String templateBasePath;

    public static final String path = "/muutosperustelut";

    @Autowired
    private MuutospyyntoService service;


    // palauttaa kaikki järjestäjän muutospyynnöt
    //@OivaAccess_Kayttaja // TODO testataan kirjautumista
    @OivaAccess_Public
    @RequestMapping(method = GET)
    @ApiOperation(notes = "Palauttaa muutosperustelut", value = "")
    public String getAll() {
        return "[\n" +
                "  {\n" +
                "    \"koodiArvo\": \"01\",\n" +
                "    \"koodisto\": {\n" +
                "      \"koodistoUri\": \"oivaperustelut\"\n" +
                "    },\n" +
                "    \"metadata\": [\n" +
                "      {\n" +
                "        \"kieli\": \"FI\",\n" +
                "        \"kuvaus\": \"\",\n" +
                "        \"nimi\": \"Muu perustelu\"\n" +
                "      },\n" +
                "       {\n" +
                "        \"kieli\": \"SV\",\n" +
                "        \"kuvaus\": \"\",\n" +
                "        \"nimi\": \"Annan argumentation\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"versio\": 1,\n" +
                "    \"voimassaAlkuPvm\": \"2018-01-01\",\n" +
                "    \"voimassaLoppuPvm\": \"\"\n" +
                "  },\n" +
                "   {\n" +
                "    \"koodiArvo\": \"02\",\n" +
                "    \"koodisto\": {\n" +
                "      \"koodistoUri\": \"oivaperustelut\"\n" +
                "    },\n" +
                "    \"metadata\": [\n" +
                "      {\n" +
                "        \"kieli\": \"FI\",\n" +
                "        \"kuvaus\": \"Väestö vanhenee\",\n" +
                "        \"nimi\": \"Väestörakenteen muutos\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"kieli\": \"SV\",\n" +
                "        \"kuvaus\": \"Väestö vanhenee (på svenska)\",\n" +
                "        \"nimi\": \"Väestörakenteen muutos (på svenska)\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"versio\": 1,\n" +
                "    \"voimassaAlkuPvm\": \"2018-01-01\",\n" +
                "    \"voimassaLoppuPvm\": \"\"\n" +
                "  }, \n"+
                "   {\n" +
                "    \"koodiArvo\": \"03\",\n" +
                "    \"koodisto\": {\n" +
                "      \"koodistoUri\": \"oivaperustelut\"\n" +
                "    },\n" +
                "    \"metadata\": [\n" +
                "      {\n" +
                "        \"kieli\": \"FI\",\n" +
                "        \"kuvaus\": \"Työttömyys nousee, tarvitaan lisäkoulutusta\",\n" +
                "        \"nimi\": \"Työttömyys nousee\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"kieli\": \"SV\",\n" +
                "        \"kuvaus\": \"Työttömyys nousee, tarvitaan lisäkoulutusta (på svenska)\",\n" +
                "        \"nimi\": \"Työttömyys nousee (på svenska)\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"versio\": 1,\n" +
                "    \"voimassaAlkuPvm\": \"2018-01-01\",\n" +
                "    \"voimassaLoppuPvm\": \"\"\n" +
                "  } \n"+
                "]";
    }


}
