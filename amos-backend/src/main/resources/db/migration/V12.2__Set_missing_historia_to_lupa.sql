-- set missing lupa references
update lupahistoria h
set lupa_id = l.id
from lupa l
where h.diaarinumero = l.diaarinumero
  and h.lupa_id is null
  and h.filename like '%/531/____';