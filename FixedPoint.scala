import chisel3._
import chisel3.util._
import chisel3.tester._
import chisel3.tester.RawTester.test
import chisel3.experimental._

object MyModule extends App {
  class Neuron(inputs: Int, act: FixedPoint => FixedPoint) extends Module {
    val io = IO(new Bundle {
      val in      = Input(Vec(inputs, FixedPoint(16.W, 8.BP)))
      val weights = Input(Vec(inputs, FixedPoint(16.W, 8.BP)))
      val out     = Output(FixedPoint(16.W, 8.BP))
    })
    
    io.out := act(io.in.zip(io.weights).map {case (in: FixedPoint, weight: FixedPoint) => in * weight}.reduce(_ + _))
  }

  val Step: FixedPoint => FixedPoint = x => Mux(x <= 0.F(8.BP), 0.F(8.BP), 1.F(8.BP))
  val ReLU: FixedPoint => FixedPoint = x => Mux(x <= 0.F(8.BP), 0.F(8.BP), x)

  // 测试Neuron
  test(new Neuron(2, Step)) { c =>
    val inputs = Seq(Seq(-1, -1), Seq(-1, 1), Seq(1, -1), Seq(1, 1))

    // 因为测试的是AND逻辑，所以权重应该是两个1
    val weights = Seq(1.0, 1.0)

    // 传入数据
    // 注意，因为是纯组合逻辑电路，因此`reset`和`step(5)`这种调用是不必要的
    for (i <- inputs) {
      c.io.in(0).poke(i(0).F(8.BP))
      c.io.in(1).poke(i(1).F(8.BP))
      c.io.weights(0).poke(weights(0).F(16.W, 8.BP))
      c.io.weights(1).poke(weights(1).F(16.W, 8.BP))
      c.io.out.expect((if (i(0) + i(1) > 0) 1 else 0).F(16.W, 8.BP))
      c.clock.step(1)
    }
  }
}