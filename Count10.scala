class Count10 extends Module {
    val io = IO(new Bundle {
        val dout = Output(UInt(8.W))
    })
    
    // 利用写好的两个模块
    val add = Module(new Adder())
    val reg = Module(new Register())
    
    // 寄存器存放的为当前的计数
    val count = reg.io.q
    
    // 当前计数和1.U作为加法器的输入
    add.io.a := 1.U
    add.io.b := count
    val result = add.io.y
    
    // 如果累加达到了10.U，则计数器清零
    val next = Mux(result === 10.U, 0.U, result)
    reg.io.d := next
    
    io.dout := count
}