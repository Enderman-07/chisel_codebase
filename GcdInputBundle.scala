import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.experimental.BundleLiterals._
import chisel3.tester._
import chisel3.tester.RawTester.test

class GcdInputBundle(val w: Int) extends Bundle {
  val value1 = UInt(w.W)
  val value2 = UInt(w.W)
}

class GcdOutputBundle(val w: Int) extends Bundle {
  val value1 = UInt(w.W)
  val value2 = UInt(w.W)
  val gcd    = UInt(w.W)
}

class DecoupledGcd(width: Int) extends MultiIOModule {

  val input = IO(Flipped(Decoupled(new GcdInputBundle(width))))
  val output = IO(Decoupled(new GcdOutputBundle(width)))

  val xInitial    = Reg(UInt())
  val yInitial    = Reg(UInt())
  val x           = Reg(UInt())
  val y           = Reg(UInt())
  val busy        = RegInit(false.B)
  val resultValid = RegInit(false.B)

  input.ready := ! busy
  output.valid := resultValid
  output.bits := DontCare

  when(busy)  {
    when(x > y) {
      x := x - y
    }.otherwise {
      y := y - x
    }
    when(y === 0.U) {
      output.bits.value1 := xInitial
      output.bits.value2 := yInitial
      output.bits.gcd := x
      resultValid := true.B
      busy := ! output.ready
    }
  }.otherwise {
    when(input.valid) {
      val bundle = input.deq()
      x := bundle.value1
      y := bundle.value2
      xInitial := bundle.value1
      yInitial := bundle.value2
      busy := true.B
      resultValid := false.B
    }
  }
}

object MyModule extends App {
  test(new DecoupledGcd(16)) { dut =>
    dut.input.initSource().setSourceClock(dut.clock)
    dut.output.initSink().setSinkClock(dut.clock)

    val testValues = for { x <- 1 to 10; y <- 1 to 10} yield (x, y)
    val inputSeq = testValues.map { case (x, y) =>
        (new GcdInputBundle(16)).Lit(_.value1 -> x.U, _.value2 -> y.U)
    }
    val resultSeq = testValues.map { case (x, y) =>
        new GcdOutputBundle(16).Lit(_.value1 -> x.U, _.value2 -> y.U, _.gcd -> BigInt(x).gcd(BigInt(y)).U)
    }

    fork {
        dut.input.enqueueSeq(inputSeq)
    }.fork {
        for (expected <- resultSeq) {
        dut.output.expectDequeue(expected)
        dut.clock.step(5) // 在接收到下一输出前等待几个周期来创建backpressure
        }
    }.join()
  }
}