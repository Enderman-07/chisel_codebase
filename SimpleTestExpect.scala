import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class SimpleTestExpect extends AnyFlatSpec with ChiselScalatestTester {
    "DUT" should "pass" in {
        test(new SimpleFsm) { dut =>
            dut.clock.step()
            println(dut.io.state.peekInt(), dut.io.light.peekBoolean())
            dut.io.start.poke(true.B)
            for (a <- 0 until 50) {
                dut.clock.step()
                dut.io.start.poke(false.B)
                println(a, dut.io.state.peekInt(), dut.io.light.peekBoolean())
            }
        }
    }
}