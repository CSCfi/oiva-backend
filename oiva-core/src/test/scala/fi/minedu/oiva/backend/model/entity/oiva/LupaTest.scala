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

  test("testEquals") {
    val lupa1 = new Lupa()
    assertResult(true, "instances should match") {
      lupa1.equals(lupa1)
    }
    val lupa2 = new Lupa()
    assertResult(false, "different instances should not match") {
      lupa1.equals(lupa2)
    }
    lupa1.setId(1L)
    lupa2.setId(1L)
    assertResult(true, "instances with same id should match") {
      lupa1.equals(lupa2)
    }
    lupa2.setId(2L)
    assertResult(false, "instances with different id should not match") {
      lupa1.equals(lupa2)
    }
  }

}
