package ml.wolfe.term

import ml.wolfe.term

/**
 * @author sameer
 * @since 4/9/15.
trait Marginalizer {
  val input: Settings
  val inputMsgs: Msgs
  val outputMsgs: Msgs

  def margs()(implicit execution: Execution)
}

trait MarginalizerFactory {
  def marginalizer(term: DoubleTerm, wrt: Seq[Var[Dom]])(obs: Settings, msgs: Msgs): Marginalizer
}


object Marginalizer {

  def sumProduct(implicit params: MaxProductParameters) = new ArgmaxerFactory {
    def argmaxer(term: DoubleTerm, wrt: Seq[Var[Dom]])(obs: Settings, msgs: Msgs) =
      new MaxProductBP(term, wrt, obs, msgs)(params)
  }

}

/**
 * @author sameer
 */
class ExhaustiveSearchMarginalizer(val obj: DoubleTerm, val wrt: Seq[Var[Dom]], val observed: Seq[Var[Dom]],
                                   val input: Settings, val inputMsgs: Msgs) extends Marginalizer {

  require(wrt.forall(_.domain.isDiscrete), "Cannot do exhaustive search over continuous domains")
  val target = obj.vars.filterNot(v => wrt.contains(v) || observed.contains(v))
  val varyingVars = (wrt ++ target).distinct

  val settingsToVary = Settings.fromSeq(varyingVars.map(_.domain.createSetting()))
  val objInput = obj.createInputSettings()

  val toVary2wrt = VariableMapping(varyingVars, wrt)
  val toVary2target = VariableMapping(varyingVars, target)
  val toVary2obj = VariableMapping(varyingVars, obj.vars)
  val obs2full = VariableMapping(observed, obj.vars)

  val allSettings = new term.AllSettings(varyingVars.map(_.domain).toIndexedSeq, settingsToVary)(_ => {})

  //link varying settings and observed settings to the input settings of the body evaluator
  toVary2obj.linkTargetsToSource(settingsToVary, objInput)
  obs2full.linkTargetsToSource(input, objInput)

  val objEval = obj.evaluatorImpl(objInput)
  val outputMsgs = Msgs(target.map(_.domain.createZeroMsg()))

  def margs()(implicit execution: Execution) = {
    for (i <- 0 until outputMsgs.length) outputMsgs(i) := Double.NegativeInfinity

    allSettings.loopSettings { settings =>
      objEval.eval()
      //add penalties from incoming messages based on current setting
      var penalized = objEval.output.cont(0)
      for ((toVaryIndex, wrtIndex) <- toVary2wrt.pairs) {
        val currentSetting = settings(toVaryIndex)
        for (i <- 0 until inputMsgs(wrtIndex).disc.length) {
          val currentValue = currentSetting.disc(i)
          val currentMsg = inputMsgs(wrtIndex).disc(i).msg(currentValue)
          penalized += currentMsg
        }
      }
      //now update outgoing messages with the max of their current value and the new score
      for ((toVaryIndex, targetIndex) <- toVary2target.pairs) {
        val currentSetting = settings(toVaryIndex)
        for (i <- 0 until outputMsgs(targetIndex).disc.length) {
          val currentValue = currentSetting.disc(i)
          val tgt = outputMsgs(targetIndex).disc(i)
          tgt.msg(currentValue) = (tgt.msg(currentValue) + penalized)
        }
      }
    }
  }

}
 */
