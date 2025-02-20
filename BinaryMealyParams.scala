import chisel3._
import chisel3.util._
import chisel3.tester._
import chisel3.tester.RawTester.test

object MyModule extends App {
  // Mealy machine has
  case class BinaryMealyParams(
    // number of states
    nStates: Int,
    // initial state
    s0: Int,
    // function describing state transition
    stateTransition: (Int, Boolean) => Int,
    // function describing output
    output: (Int, Boolean) => Int
  ) {
    require(nStates >= 0)
    require(s0 < nStates && s0 >= 0)
  }

  class BinaryMealy(val mp: BinaryMealyParams) extends Module {
    val io = IO(new Bundle {
      val in = Input(Bool())
      val out = Output(UInt())
    })

    val state = RegInit(UInt(), mp.s0.U)

    // output zero if no states
    io.out := 0.U
    for (i <- 0 until mp.nStates) {
      when (state === i.U) {
        when (io.in) {
          state  := mp.stateTransition(i, true).U
          io.out := mp.output(i, true).U
        }.otherwise {
          state  := mp.stateTransition(i, false).U
          io.out := mp.output(i, false).U
        }
      }
    }
  }

  val nStates = 3
  val s0 = 2
  def stateTransition(state: Int, in: Boolean): Int = {
    if (in) {
      1
    } else {
      0
    }
  }
  def output(state: Int, in: Boolean): Int = {
    if (state == 2) {
      return 0
    }
    if ((state == 1 && !in) || (state == 0 && in)) {
      return 1
    } else {
      return 0
    }
  }

  val testParams = BinaryMealyParams(nStates, s0, stateTransition, output)

  test(new BinaryMealy(testParams)) { c =>
    c.io.in.poke(false.B)
    c.io.out.expect(0.U)
    c.clock.step(1)
    c.io.in.poke(false.B)
    c.io.out.expect(0.U)
    c.clock.step(1)
    c.io.in.poke(false.B)
    c.io.out.expect(0.U)
    c.clock.step(1)
    c.io.in.poke(true.B)
    c.io.out.expect(1.U)
    c.clock.step(1)
    c.io.in.poke(true.B)
    c.io.out.expect(0.U)
    c.clock.step(1)
    c.io.in.poke(false.B)
    c.io.out.expect(1.U)
    c.clock.step(1)
    c.io.in.poke(true.B)
    c.io.out.expect(1.U)
    c.clock.step(1)
    c.io.in.poke(false.B)
    c.io.out.expect(1.U)
    c.clock.step(1)
    c.io.in.poke(true.B)
    c.io.out.expect(1.U)
  }

  println("SUCCESS!!") // Scala Code: if we get here, our tests passed!
}