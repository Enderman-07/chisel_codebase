class RegisterFile(readPorts: Int) extends Module {
  require(readPorts >= 0)
  val io = IO(new Bundle{
    val wen   = Input(Bool())
    val waddr = Input(UInt(5.W))
    val wdata = Input(UInt(32.W))
    val raddr = Input(Vec(readPorts, UInt(5.W)))
    val rdata = Output(Vec(readPorts, UInt(32.W)))
  })

  // 32个  初始化为0的32位数  形成数组  构成寄存器
  val reg = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

  when (io.wen) {
    reg(io.waddr) := io.wdata
  }

  for (i <- 0 until readPorts) {
    when (io.raddr(i) === 0.U) {
      io.rdata(i) := 0.U
    } .otherwise {
      io.rdata(i) := reg(io.raddr(i))
    }
  }
}

// test part
<!-- test(new RegisterFile(2) ) { c =>
  def readExpect(addr: Int, value: Int, port: Int = 0): Unit = {
    c.io.raddr(port).poke(addr.U)
    c.io.rdata(port).expect(value.U)
  }
  def write(addr: Int, value: Int): Unit = {
    c.io.wen.poke(true.B)
    c.io.wdata.poke(value.U)
    c.io.waddr.poke(addr.U)
    c.clock.step(1)
    c.io.wen.poke(false.B)
  }
  // 每个寄存器都应该是初始化为0的
  for (i <- 0 until 32) {
    readExpect(i, 0, port = 0)
    readExpect(i, 0, port = 1)
  }

  // 往每个寄存器写入值 5 * addr + 3
  for (i <- 0 until 32) {
    write(i, 5 * i + 3)
  }

  // 检查有没有写进去，寄存器0始终读到0
  for (i <- 0 until 32) {
    readExpect(i, if (i == 0) 0 else 5 * i + 3, port = i % 2)
  }
} -->