import chisel3._
import chisel3.util._
import chisel3.tester._
import chisel3.tester.RawTester.test

/** Sort4 sorts its 4 inputs to its 4 outputs */
class Sort4(ascending: Boolean) extends Module {
  val io = IO(new Bundle {
    val in0 = Input(UInt(16.W))
    val in1 = Input(UInt(16.W))
    val in2 = Input(UInt(16.W))
    val in3 = Input(UInt(16.W))
    val out0 = Output(UInt(16.W))
    val out1 = Output(UInt(16.W))
    val out2 = Output(UInt(16.W))
    val out3 = Output(UInt(16.W))
  })
    
  // this comparison funtion decides < or > based on the module's parameterization
  def comp(l: UInt, r: UInt): Bool = {
      if (ascending) {
        l < r
      } else {
        l > r
    }
  }

  val row10 = Wire(UInt(16.W))
  val row11 = Wire(UInt(16.W))
  val row12 = Wire(UInt(16.W))
  val row13 = Wire(UInt(16.W))

  when(comp(io.in0, io.in1)) {
    row10 := io.in0            // preserve first two elements
    row11 := io.in1
  }.otherwise {
    row10 := io.in1            // swap first two elements
    row11 := io.in0
  }

  when(comp(io.in2, io.in3)) {
    row12 := io.in2            // preserve last two elements
    row13 := io.in3
  }.otherwise {
    row12 := io.in3            // swap last two elements
    row13 := io.in2
  }

  val row21 = Wire(UInt(16.W))
  val row22 = Wire(UInt(16.W))

  when(comp(row11, row12)) {
    row21 := row11            // preserve middle 2 elements
    row22 := row12
  }.otherwise {
    row21 := row12            // swap middle two elements
    row22 := row11
  }

  val row20 = Wire(UInt(16.W))
  val row23 = Wire(UInt(16.W))
  when(comp(row10, row13)) {
    row20 := row10            // preserve the first and the forth elements
    row23 := row13
  }.otherwise {
    row20 := row13            // swap the first and the forth elements
    row23 := row10
  }

  when(comp(row20, row21)) {
    io.out0 := row20            // preserve first two elements
    io.out1 := row21
  }.otherwise {
    io.out0 := row21            // swap first two elements
    io.out1 := row20
  }

  when(comp(row22, row23)) {
    io.out2 := row22            // preserve first two elements
    io.out3 := row23
  }.otherwise {
    io.out2 := row23            // swap first two elements
    io.out3 := row22
  }
}

object MyModule extends App {
  // Here are the testers
  test(new Sort4(true)) { c => 
    c.io.in0.poke(3.U)
    c.io.in1.poke(6.U)
    c.io.in2.poke(9.U)
    c.io.in3.poke(12.U)
    c.io.out0.expect(3.U)
    c.io.out1.expect(6.U)
    c.io.out2.expect(9.U)
    c.io.out3.expect(12.U)

    c.io.in0.poke(13.U)
    c.io.in1.poke(4.U)
    c.io.in2.poke(6.U)
    c.io.in3.poke(1.U)
    c.io.out0.expect(1.U)
    c.io.out1.expect(4.U)
    c.io.out2.expect(6.U)
    c.io.out3.expect(13.U)

    c.io.in0.poke(13.U)
    c.io.in1.poke(6.U)
    c.io.in2.poke(4.U)
    c.io.in3.poke(1.U)
    c.io.out0.expect(1.U)
    c.io.out1.expect(4.U)
    c.io.out2.expect(6.U)
    c.io.out3.expect(13.U)
  }
  test(new Sort4(false)) { c =>
    c.io.in0.poke(3.U)
    c.io.in1.poke(6.U)
    c.io.in2.poke(9.U)
    c.io.in3.poke(12.U)
    c.io.out0.expect(12.U)
    c.io.out1.expect(9.U)
    c.io.out2.expect(6.U)
    c.io.out3.expect(3.U)

    c.io.in0.poke(13.U)
    c.io.in1.poke(4.U)
    c.io.in2.poke(6.U)
    c.io.in3.poke(1.U)
    c.io.out0.expect(13.U)
    c.io.out1.expect(6.U)
    c.io.out2.expect(4.U)
    c.io.out3.expect(1.U)

    c.io.in0.poke(1.U)
    c.io.in1.poke(6.U)
    c.io.in2.poke(4.U)
    c.io.in3.poke(13.U)
    c.io.out0.expect(13.U)
    c.io.out1.expect(6.U)
    c.io.out2.expect(4.U)
    c.io.out3.expect(1.U)
  }
  println("SUCCESS!!") // Scala Code: if we get here, our tests passed!
}