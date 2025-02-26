class BubbleFifo(size: Int, depth: Int) extends Module {
    val io = IO(new Bundle {
        val enq = new WriterIO(size)
        val deq = new ReaderIO(size)
    })

    val buffers = Array.fill(depth) {
        Module(new FIFORegister(size))
    }

    for (i <- 0 until depth - 1) {
        buffers(i + 1).io.enq.din := buffers(i).io.deq.dout
        buffers(i + 1).io.enq.write := buffers(i).io.deq.read
        buffers(i).io.deq.read := ~buffers(i + 1).io.enq.full
    }

    io.enq <> buffers(0).io.enq
    io.deq <> buffers(depth - 1).io.deq
}