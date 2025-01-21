import chisel3._
import chisel3.util.BitPat
import chisel3.util.experimental.decode._

class SimpleDecoder extends Module {
  // 定义真值表
  val table = TruthTable(
    Map(
      BitPat("b001") -> BitPat("b?"), // 输入 "001" 对应输出 "?"
      BitPat("b010") -> BitPat("b?"), // 输入 "010" 对应输出 "?"
      BitPat("b100") -> BitPat("b1"), // 输入 "100" 对应输出 "1"
      BitPat("b101") -> BitPat("b1"), // 输入 "101" 对应输出 "1"
      BitPat("b111") -> BitPat("b1")  // 输入 "111" 对应输出 "1"
    ),
    BitPat("b0") // 默认输出为 "0"
  )

  // 定义输入输出端口
  val input = IO(Input(UInt(3.W)))  // 3位输入
  val output = IO(Output(UInt(1.W))) // 1位输出

  // 使用真值表解码输入
  output := decoder(input, table)
}
