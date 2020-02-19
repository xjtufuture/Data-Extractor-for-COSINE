/*
版权所属：任五岳
代码及思路仅供参考使用。
联系方式：13918998524   mail:xjtufuture@163.com
 */



import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.swing.*
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.collections.HashMap


var outPut = ""

var stream : InputStream = InputStream.nullInputStream()


var filepath = System.getProperty("user.dir")
//var filepath = "D:\\任五岳2041工作\\COSINE验证\\程序计算\\cosCSLOCA-V1.3 - ssdone\\output"
var fileNames: MutableList<String> = mutableListOf()
var filePaths: MutableList<String> = mutableListOf()

var filePathMaps = HashMap<String,String>()
var parameterMaps = HashMap<String, Array<String>>()


var list1 = JList<Any?>()
var list2 = JList<Any?>()
var v = Vector<String>()
var k = Vector<String>()




fun treeFile(){
    val fileTree :FileTreeWalk = File(filepath).walk()
    fileTree.maxDepth(3)
        .filter { it.isFile }
        .filter { it.extension == "plt" }
        .filter { filePaths.add(it.absolutePath) }
        .filter { fileNames.add(it.name) }
        .forEach { filePathMaps[it.name] = it.absolutePath; parameterStrip(it.name,it.absolutePath)}
}


fun main(args : Array<String>){

    treeFile()
    val frame = JFrame("欢迎使用COSINE数据读取器-任五岳制作")
    val contentPane = frame.contentPane
    contentPane.layout = GridLayout(1,2)



    val resultFrame = JFrame("抽取案例的样本")
    val contentPane2 = resultFrame.contentPane

    val resultArea  = JTextArea()
    resultArea.text = ""
    val scrollPane2 = JScrollPane(resultArea)
    contentPane2.add(scrollPane2)

    val menuBar = JMenuBar()
    resultFrame.jMenuBar = menuBar

    val fileMenu = JMenu("打开/保存配置文件")
    val openFile = JMenuItem("打开现有配置文件")
    openFile.addActionListener(ActionListener {
        val chooser = JFileChooser(System.getProperty("user.dir"))
        val f = FileNameExtensionFilter("配置文件(*.txt)","txt")
        chooser.fileFilter = f
        if (chooser.showOpenDialog(openFile)==JFileChooser.APPROVE_OPTION){
            val file = chooser.selectedFile
//            resultArea.font = Font("Courier New", Font.PLAIN, 15)

//                textArea.text = file.bufferedReader(charset("gbk")).readText()

            resultArea.text = file.readText(charset("UTF-8"))
        }
    })


    val saveFile = JMenuItem("保存配置文件")
    saveFile.addActionListener(ActionListener{
        val chooser = JFileChooser(System.getProperty("user.dir"))
        chooser.selectedFile = File("COSINE输出配置文件.txt")
        chooser.fileFilter = FileNameExtensionFilter("输出文件(*.txt)",".txt")
        if (chooser.showSaveDialog(saveFile)==JFileChooser.APPROVE_OPTION){
            val file= chooser.selectedFile
            val fos = FileOutputStream(file)
            fos.write(resultArea.text.toByteArray(charset("UTF-8")))
        }
    })

    val stripMenu = JMenu("抽取数据结果")
    val stripFile = JMenuItem("结果抽取")
    stripFile.addActionListener(ActionListener{



        val stripOutString = resultArea.text
        store(stripOutString)
        val tt = System.currentTimeMillis()
        val file = File(System.getProperty("user.dir"),"COSINE数据抽取结果 By 五岳-$tt.txt")
        val rr = turn(outPut)
//        file.writeText(outPut)
        file.writeText(rr.replace("null"," "))
    })

    val thanksMenu = JMenu("软件信息")
    val information  = JMenuItem("软件信息")
    information.addActionListener(ActionListener {
        val jumpout = JFrame()
        jumpout.setSize(400,400)
        JOptionPane.showMessageDialog(jumpout,
            "软件版本：1.0 \n" +
                    "基础语言：Kotlin \n"
                    )

    })

    val thanks = JMenuItem("鸣谢")
    thanks.addActionListener(ActionListener {
        val jumpout = JFrame()
        jumpout.setSize(400,400)
        JOptionPane.showMessageDialog(jumpout,
            "感谢苏老师对我的教诲。\n" +
                    "感谢西安交通大学NUTHEL热工水力课题组！\n" +
                    "学习与交流---xjtufuture@163.com")

    })


    menuBar.add(fileMenu)
    menuBar.add(stripMenu)
    menuBar.add(thanksMenu)

    fileMenu.add(openFile)
    fileMenu.add(saveFile)

    stripMenu.add(stripFile)

    thanksMenu.add(information)
    thanksMenu.add(thanks)



    resultFrame.contentPane.add(scrollPane2, BorderLayout.CENTER)
    resultFrame.setSize(500,200)
//    resultFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    resultFrame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
    resultFrame.setLocationRelativeTo(frame.layeredPane)
    resultFrame.isVisible = true



    v = Vector()
    for (index in 0 until fileNames.size){
        v.addElement(fileNames[index])
    }

    var leftName = ""


    list1 = JList(v)
    list2 = JList(k)



    list1.border = BorderFactory.createTitledBorder("选择要提取数据的文件")
    list2.border = BorderFactory.createTitledBorder("选择要提取的参数")

    contentPane.add(JScrollPane(list1))
    contentPane.add(JScrollPane(list2))


    list1.addMouseListener(object : MouseAdapter(){
        override fun mouseClicked(e: MouseEvent){

            k = Vector()
            var index  : Int

            if (e.source == list1){
                if (e.clickCount % 2==0){

                    println("in")
                    index = list1.locationToIndex(e.point)
                    leftName = v.elementAt(index)

                    val list2Name = parameterMaps[leftName]

                    for (indN in 0 until list2Name!!.size){
                        k.addElement(list2Name[indN])
                    }
                    list2 = JList(k)

                    list2.border = BorderFactory.createTitledBorder("选择要提取的参数")
                    list1.border = BorderFactory.createTitledBorder("选择要提取数据的文件")


                    contentPane.removeAll()
                    contentPane.repaint()


                    contentPane.add(JScrollPane(list1))
                    contentPane.add(JScrollPane(list2))
                    contentPane.revalidate()


                }
            }

            list2.addMouseListener(object : MouseAdapter(){
                override fun mouseClicked(e: MouseEvent){
                    val indexNumber: Int
                    val rightName: String
                    if (e.source == list2){

                        if (e.clickCount == 2){

                            e.clickCount
                            indexNumber = list2.locationToIndex(e.point)

                            rightName = parameterMaps[leftName]!![indexNumber]

                            resultArea.text += leftName +"------->"+rightName + "------->"+ indexNumber + "\n"


                        }
                    }
                }
            })
        }
    })
    frame.setLocation(500,100)
    frame.pack()
    frame.isVisible = true
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

}

fun store(textString :String){
    outPut = ""

    textString.byteInputStream().bufferedReader().forEachLine {

        val oneLine = it.trim().split("------->".toRegex()).toTypedArray()
        if (oneLine.size == 3){
            strip(oneLine[0],oneLine[2].toInt())
            outPut += "\n"
        }else{
            println("NONONO")
        }
    }
}


fun strip(Name : String, index : Int){


    fileNames = mutableListOf()
    filePaths = mutableListOf()


    val fileTree :FileTreeWalk = File(filepath).walk()
    fileTree.maxDepth(3)
        .filter { it.isFile }
        .filter { it.extension in "plt" }
        .filter { it.name == Name }
        .forEach { filePathMaps[it.name] = it.absolutePath;  parameterStrip(it.name,it.absolutePath)}


    val openTheFile = File(filePathMaps[Name]).toString()
    stream = "".byteInputStream()
    stream = Files.newInputStream(Path.of(openTheFile))
    stream.bufferedReader().forEachLine {
        val oneLine = it.trim().split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        outPut += (oneLine[index]+"   ")
    }
}


fun parameterStrip(name : String,url : String){
    val readFile = File(url).toString()
    stream = Files.newInputStream(Path.of(readFile))
    var lN = 0
    stream.bufferedReader().forEachLine {

        if (lN == 0){
            val oneLine = it.substringBefore("\n").trim().split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            parameterMaps[name] = oneLine
        }
        lN += 1

    }
}

var s = ""
var dataArray = arrayOfNulls<String>(99999)

fun turn(string: String):String{
    dataArray = arrayOfNulls(99999)
    s = ""

    var length :  Int = 0
    var height :  Int = 0


    string.byteInputStream().bufferedReader().forEachLine {
        val oneLine = it.trim().split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        height = oneLine.size
        length += 1
    }

    println("Height: $height")
    println("length: $length")
    for (i in 0 until height-1){
        string.byteInputStream().bufferedReader().forEachLine {
            val oneLine = it.trim().split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            dataArray[i] += oneLine[i]
            for (o in 1 until 20-oneLine[i].count()){
                dataArray[i] += " "
            }
        }
    }
    for (j in 0 until  height-1){
        s += dataArray[j] +"\n"
    }


    return s




}




