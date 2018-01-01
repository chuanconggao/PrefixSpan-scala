import scala.collection.mutable.{ArrayBuffer, BitSet, HashMap, PriorityQueue}

class Miner(val db: Array[Array[Int]]) {
  type Matches = ArrayBuffer[(Int, Int)]
  type Pattern = Vector[Int]
  type Results = ArrayBuffer[(Int, Pattern)]

  private def scan(matches: Matches) = {
    val allOccurs = new HashMap[Int, Matches]

    for ((i, pos) <- matches) {
      val seq = db(i)

      val occurs = new BitSet
      for (j <- pos until seq.length) {
        val k = seq(j)
        if (occurs.add(k)) {
          allOccurs.getOrElseUpdate(k, new Matches).append((i, j + 1))
        }
      }
    }

    allOccurs
  }

  def mineFrequent(minSup: Int)(minLen: Int = 1, maxLen: Int = Int.MaxValue): Results = {
    val results = new Results

    def mine(patt: Pattern, matches: Matches): Unit = {
      if (patt.length >= minLen) {
        results.append((matches.length, patt))
      }

      if (patt.length < maxLen) {
        for ((c, newMatches) <- scan(matches)) {
          if (newMatches.length >= minSup) {
            mine(patt :+ c, newMatches)
          }
        }
      }
    }

    mine(Vector(), (0 until db.length).map((_, 0)).to[ArrayBuffer])

    results
  }

  def mineTopK(k: Int)(minLen: Int = 1, maxLen: Int = Int.MaxValue): Results = {
    val heap = PriorityQueue.empty[(Int, Pattern)](Ordering.by(- _._1))

    def mine(patt: Pattern, matches: Matches): Unit = {
      if (patt.length >= minLen) {
        heap.enqueue((matches.length, patt))
        if (heap.length > k) {
          heap.dequeue()
        }
      }

      if (patt.length < maxLen) {
        for ((c, newMatches) <- scan(matches).toArray.sortBy(- _._2.length)) {
          if (heap.length == k && newMatches.length <= heap.head._1) {
              return
          }

          mine(patt :+ c, newMatches)
        }
      }
    }

    mine(Vector(), (0 until db.length).map((_, 0)).to[ArrayBuffer])

    heap.dequeueAll.reverse.to[ArrayBuffer]
  }
}

object PrefixSpan extends App {
  def printUsage(): Unit = {
    Console.err.println(
      """
Usage:
    scala PrefixSpan <threshold> [<minlen>=1] [<maxlen>=maxint]
      """
    )

    System.exit(1)
  }

  val miner = new Miner(
    io.Source.stdin.getLines()
      .map(
        _.split(' ').map(_.toInt).toArray
      )
      .toArray
  )

  if (args.isEmpty) {
    printUsage()
  }

  val f = args(0) match {
    case "frequent" => miner.mineFrequent _
    case "top-k" => miner.mineTopK _
    case _ => null
  }
  if (f == null) {
    printUsage()
  }

  val threshold = args(1).toInt
  val minLen = if (args.length > 2) args(2).toInt else 1
  val maxLen = if (args.length > 3) args(3).toInt else Int.MaxValue

  for ((freq, patt) <- f(threshold)(minLen, maxLen)) {
    println(s"${patt.mkString(" ")} : $freq")
  }
}
