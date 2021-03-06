object Test extends App {

  import scala.collection.immutable._

  // finds an int x such that improved(x) differs in the first bit to improved(0),
  // which is the worst case for the HashTrieSet
  def findWorstCaseInts(): Unit = {
    // copy of improve from HashSet
    def improve(hcode: Int) = {
      var h: Int = hcode + ~(hcode << 9)
      h = h ^ (h >>> 14)
      h = h + (h << 4)
      h ^ (h >>> 10)
    }

    // find two hashes which have a large separation
    val x = 0
    var y = 1
    val ix = improve(x)
    while(y!=0 && improve(y)!=ix+(1<<31))
      y+=1
    printf("%s %s %x %x\n",x,y,improve(x), improve(y))
  }
  // this is not done every test run since it would slow down ant test.suite too much.
  // findWorstCaseInts()

  // two numbers that are immediately adiacent when fed through HashSet.improve
  val h0 = 0
  val h1 = 1270889724

  // h is the hashcode, i is ignored for the hashcode but relevant for equality
  case class Collision(h:Int, i:Int) {
    override def hashCode = h
  }
  val a = Collision(h0,0)
  val b = Collision(h0,1)
  val c = Collision(h1,0)

  // create a HashSetCollision1
  val x = OldHashSet(a) + b
  if(x.getClass.getSimpleName != "OldHashSetCollision1")
    println("x should be a collision")
  StructureTests.validate(x)
  // StructureTests.printStructure(x)
  require(x.size==2 && x.contains(a) && x.contains(b))

  // go from a HashSetCollision1 to a HashTrieSet with maximum depth
  val y = x + c
  if(y.getClass.getSimpleName != "HashTrieSet")
    println("y should be a HashTrieSet")
  StructureTests.validate(y)
  // StructureTests.printStructure(y)
  require(y.size==3 && y.contains(a) && y.contains(b) && y.contains(c))

  // go from a HashSet1 directly to a HashTrieSet with maximum depth
  val z = OldHashSet(a) + c
  if(y.getClass.getSimpleName != "HashTrieSet")
    println("y should be a HashTrieSet")
  StructureTests.validate(z)
  // StructureTests.printStructure(z)
  require(z.size == 2 && z.contains(a) && z.contains(c))
}

package scala.collection.immutable {
  object StructureTests {
    def printStructure(x:OldHashSet[_], prefix:String=""): Unit = {
      x match {
        case m:OldHashSet.HashTrieSet[_] =>
          println(prefix+m.getClass.getSimpleName + " " + m.size)
          m.elems.foreach(child => printStructure(child, prefix + "  "))
        case m:OldHashSet.OldHashSetCollision1[_] =>
          println(prefix+m.getClass.getSimpleName + " " + m.ks.size)
        case m:OldHashSet.OldHashSet1[_] =>
          println(prefix+m.getClass.getSimpleName + " " + m.head)
        case _ =>
          println(prefix+"empty")
      }
    }

    def validate(x:OldHashSet[_]): Unit = {
      x match {
        case m:OldHashSet.HashTrieSet[_] =>
          require(m.elems.size>1 || (m.elems.size==1 && m.elems(0).isInstanceOf[OldHashSet.HashTrieSet[_]]))
          m.elems.foreach(validate _)
        case m:OldHashSet.OldHashSetCollision1[_] =>
          require(m.ks.size>1)
        case m:OldHashSet.OldHashSet1[_] =>
        case _ =>
      }
    }
  }
}
