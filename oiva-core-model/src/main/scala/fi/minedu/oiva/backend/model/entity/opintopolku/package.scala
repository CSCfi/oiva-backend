package fi.minedu.oiva.backend.model.entity

import com.fasterxml.jackson.annotation.JsonRawValue

import scala.annotation.meta.field

package object entity {
    type JsonRawValueField = JsonRawValue @field
}
