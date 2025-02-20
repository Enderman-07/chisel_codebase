import chisel3._
import scala.io.Source

class FileReader extends Module {
    val io = IO(new Bundle {
        val address = Input(UInt(8.W))
        val data = Output(UInt(8.W))
    })
    
    val array = new Array[Int](256)
    var idx = 0
    
    // 读取文件到Scala数组
    val source = Source.fromfile("data.txt")
    for (line <- source.getLines()) {
        array(idx) = line.toInt
        idx += 1
    }
    
    // 把Scala整数数组转换为Chisel UInt的Vec
    val table = VecInit(array.map(_.U(8.W)))
    
    // 使用真值表
    io.data := table(io.address)
}