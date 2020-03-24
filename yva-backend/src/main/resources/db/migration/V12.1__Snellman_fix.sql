update maarays
set meta = '{
  "oppilaitoksentarkoitus-0": "Snellman-korkeakoulun tarkoituksena on edistää opiskelijoiden inhimillistä ja ammatillista kehitystä. Lähtökohtana on laaja-alainen käsitys sivistyksestä, johon kuuluu filosofisia, tieteellisiä, taiteellisia ja eettis-käytännöllisiä opintoja. Toiminnassa painottuu steinerpedagoginen ja fenomenologinen lähestymistapa, joka pyrkii ottamaan huomioon myös ihmisen ja maailman henkisen ulottuvuuden. Koulutuksen tueksi kehitetään siihen liittyvää tutkimusta ja pyritään laajaan kansalliseen ja kansainväliseen yhteistoimintaan, varsinkin vuorovaikutukseen muiden opettajankoulutuslaitosten kanssa."
}'
from lupa l
where l.diaarinumero = '44/440/2002'
  and l.id = lupa_id
  and kohde_id = (select id from kohde where tunniste = 'tarkoitus');