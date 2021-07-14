package covid360rf.covid360.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivityQuestionBinding
import covid360rf.covid360.model.Questiondata
import covid360rf.covid360.utils.setData

class QuestionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuestionBinding
    private var score:Int=0

    private var currentPosition:Int=1
    private var questionList:ArrayList<Questiondata> ? = null
    private var selecedOption:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        questionList = setData.getQuestion()

        setQuestion()

        binding.idOption1.setOnClickListener{
            selectedOptionStyle(binding.idOption1,1)
        }
        binding.idOption2.setOnClickListener{
            selectedOptionStyle(binding.idOption2,2)
        }

        binding.idBtnsubmit.setOnClickListener {
            if(selecedOption!=0)
            {
                val question=questionList!![currentPosition-1]

                if(selecedOption==question.answer)
                    score++

                setColor(question.answer,R.drawable.correct_ans)
                if(currentPosition == questionList!!.size)
                    binding.idBtnsubmit.text = "FINISH"
                else
                    binding.idBtnsubmit.text ="Go to Next"
            }else{
                currentPosition++
                when{
                    currentPosition <= questionList!!.size ->{
                        setQuestion()
                    }
                    else->{
                        val intent= Intent(this,ResultActivity::class.java)

                        intent.putExtra(setData.score,score.toString())
                        intent.putExtra("total size",questionList!!.size.toString())

                        startActivity(intent)
                        finish()
                    }
                }
            }
            selecedOption=0
        }

    }

    private fun setColor(opt:Int, color:Int){
        when(opt){
            1->{
                binding.idOption1.background=ContextCompat.getDrawable(this,color)
            }
            2->{
                binding.idOption2.background=ContextCompat.getDrawable(this,color)
            }

        }
    }

    private fun setQuestion(){

        val question = questionList!![currentPosition-1]
        setOptionStyle()

        binding.idProgess.progress=currentPosition
        binding.idProgess.max=questionList!!.size
        binding.idquestion.text=question.question
        binding.idOption1.text=question.option_one
        binding.idOption2.text=question.option_two
    }

    private fun setOptionStyle(){
        val optionList:ArrayList<TextView> = arrayListOf()
        optionList.add(0,binding.idOption1)
        optionList.add(1,binding.idOption2)

        for(op in optionList)
        {
            op.setTextColor(Color.parseColor("#555151"))
            op.background = ContextCompat.getDrawable(this,R.drawable.question_option)
            op.typeface = Typeface.DEFAULT
        }
    }

    private fun selectedOptionStyle(view:TextView, opt:Int){
        setOptionStyle()
        selecedOption=opt
        view.background=ContextCompat.getDrawable(this,R.drawable.option_selected_bg)
        view.typeface= Typeface.DEFAULT_BOLD
        view.setTextColor(Color.parseColor("#000000"))
    }
}