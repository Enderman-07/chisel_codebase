import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

trait TickerTestFunc {
    def testFn[T <: Ticker](dut: T, n: Int) = {
        // -1表示还没得到任何tick
        var count = -1
        for (_ <- 0 to n * 3) {
            // 检查输出是否正确
            if (count > 0)
            	dut.io.tick.expect(false.B)
            else if (count == 0)
            	dut.io.tick.expect(true.B)
        
            // 在每个tick上重置计数器
            if (dut.io.tick.peek.litToBoolean)
                count = n-1
            else
                count -= 1
            dut.clock.step()
        }
    }
}