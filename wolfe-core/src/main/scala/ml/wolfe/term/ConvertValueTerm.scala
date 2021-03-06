package ml.wolfe.term

/**
 * @author riedel
 */
trait ConvertValueTerm[A <: Term[Dom], D <: Dom] extends Term[D] with NAry {
  val term:A
  val domain:D
  def f(arg:term.domain.Value):domain.Value



  type ArgumentType = A

  def arguments = IndexedSeq(term)

  private var currentExecution:Execution = null
  private var mapped:domain.Value = null.asInstanceOf[domain.Value]
  private var inputValue:term.domain.Value = _

  def vars = term.vars

  override def evaluatorImpl(in: Settings) = new AbstractEvaluator(in) {
    val termEval = term.evaluatorImpl(in)


    def updateInput()(implicit execution: Execution):Boolean = {
      termEval.eval()
      val oldValue = inputValue
      inputValue = term.domain.toValue(termEval.output)
      oldValue != inputValue
    }


    def eval()(implicit execution: Execution): Unit = {
      if (updateInput() || currentExecution != execution) {
        //updateInput()
        mapped = f(inputValue)
        currentExecution = execution
      }
      domain.copyValue(mapped, output)
    }
    val output = domain.createSetting()
  }

  override def toString = s"TermMap($term)"
}

trait ConvertValuesTerm[A <: Term[Dom], D <: Dom] extends Term[D] {
  val term:A
  val domain:D
  def f(arg:term.domain.Value):Term[D]

  lazy val prototype = f(term.domain.zero)

  lazy val this2proto = VariableMapping(vars,prototype.vars)


  def vars = (term.vars ++ prototype.vars).distinct

  override def evaluatorImpl(in: Settings) = new AbstractEvaluator(in) {
    val termEval = term.evaluatorImpl(in)
    var currentExecution:Execution = null
    def eval()(implicit execution: Execution): Unit = {
      if (currentExecution != execution) {
        termEval.eval()
        val value = term.domain.toValue(termEval.output)
        val mapped = f(value)
        val mappedEval = mapped.evaluatorImpl(input.linkedSettings(vars,mapped.vars))
        mappedEval.eval()
        output shallowAssign mappedEval.output
      }
    }
    val output = domain.createSetting()
  }

}

