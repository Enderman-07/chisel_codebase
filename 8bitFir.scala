//8-bit规格的四元素FIR滤波器实现
// MyModule.scala
import chisel3._
import chisel3.util._

class MyModule(b0: Int, b1: Int, b2: Int, b3: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  val x_n1 = RegNext(io.in, 0.U)
  val x_n2 = RegNext(x_n1, 0.U)
  val x_n3 = RegNext(x_n2, 0.U)
  io.out := io.in * b0.U + x_n1 * b1.U + x_n2 * b2.U + x_n3 * b3.U
}

object MyModule extends App {
  println(getVerilogString(new MyModule(0, 0, 0, 0)))
}

// MyModuleTest.scala
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class MyModuleTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "MyModule"
  it should "get right results" in {
    // Simple sanity check: a element with all zero coefficients should always produce zero
    test(new MyModule(0, 0, 0, 0)) { c =>
        c.io.in.poke(0.U)
        c.io.out.expect(0.U)
        c.clock.step(1)
        c.io.in.poke(4.U)
        c.io.out.expect(0.U)
        c.clock.step(1)
        c.io.in.poke(5.U)
        c.io.out.expect(0.U)
        c.clock.step(1)
        c.io.in.poke(2.U)
        c.io.out.expect(0.U)
    }
    // Simple 4-point moving average
    test(new MyModule(1, 1, 1, 1)) { c =>
        c.io.in.poke(1.U)
        c.io.out.expect(1.U)  // 1, 0, 0, 0
        c.clock.step(1)
        c.io.in.poke(4.U)
        c.io.out.expect(5.U)  // 4, 1, 0, 0
        c.clock.step(1)
        c.io.in.poke(3.U)
        c.io.out.expect(8.U)  // 3, 4, 1, 0
        c.clock.step(1)
        c.io.in.poke(2.U)
        c.io.out.expect(10.U)  // 2, 3, 4, 1
        c.clock.step(1)
        c.io.in.poke(7.U)
        c.io.out.expect(16.U)  // 7, 2, 3, 4
        c.clock.step(1)
        c.io.in.poke(0.U)
        c.io.out.expect(12.U)  // 0, 7, 2, 3
    }
    // Nonsymmetric filter
    test(new MyModule(1, 2, 3, 4)) { c =>
        c.io.in.poke(1.U)
        c.io.out.expect(1.U)  // 1*1, 0*2, 0*3, 0*4
        c.clock.step(1)
        c.io.in.poke(4.U)
        c.io.out.expect(6.U)  // 4*1, 1*2, 0*3, 0*4
        c.clock.step(1)
        c.io.in.poke(3.U)
        c.io.out.expect(14.U)  // 3*1, 4*2, 1*3, 0*4
        c.clock.step(1)
        c.io.in.poke(2.U)
        c.io.out.expect(24.U)  // 2*1, 3*2, 4*3, 1*4
        c.clock.step(1)
        c.io.in.poke(7.U)
        c.io.out.expect(36.U)  // 7*1, 2*2, 3*3, 4*4
        c.clock.step(1)
        c.io.in.poke(0.U)
        c.io.out.expect(32.U)  // 0*1, 7*2, 2*3, 3*4
    }
    println("SUCCESS!!")
  }
}