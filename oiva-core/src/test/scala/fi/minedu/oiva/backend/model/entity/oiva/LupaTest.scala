package fi.minedu.oiva.backend.model.entity.oiva

import org.scalatest.FunSuite

class LupaTest extends FunSuite {

  test("testGetPDFFileName with diaarinumero") {
    val lupa = new Lupa()
    lupa.setDiaarinumero("1/2/1234")
    assertResult("lupa-1-2-1234.pdf") {
      lupa.getPDFFileName
    }
  }

  test("testGetPDFFileName with asianumero") {
    val lupa = new Lupa()
    lupa.setAsianumero("VN/1/2/1234")
    assertResult("lupa-VN-1-2-1234.pdf") {
      lupa.getPDFFileName
    }
  }

}
