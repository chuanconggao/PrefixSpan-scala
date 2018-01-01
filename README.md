The shortest yet efficient implementation of [PrefixSpan](http://www.cs.sfu.ca/~jpei/publications/span.pdf) in Scala. You can find the Python 3 version [here](https://github.com/chuanconggao/PrefixSpan-py).

# Usage
```
scala PrefixSpan (frequent | top-k) <threshold> [<minlen>=1] [<maxlen>=maxint]
```

  * Sequences are read from standard input. Each sequence is integers separated by space, like this example:
```
0 1 2 3 4
1 1 1 3 4
2 1 2 2 0
1 1 1 2 2
```

  * The patterns and their respective frequencies are printed to standard output.

# Features
Outputs traditional single-item sequential patterns, where gaps are allowed between items.

  * Mining top-k patterns is also supported, with respective optimizations.
  * You can also limit the length of mined patterns. Note that setting maximum pattern length properly can significantly speedup the algorithm.
