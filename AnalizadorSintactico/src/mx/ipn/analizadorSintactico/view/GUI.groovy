package mx.ipn.analizadorSintactico.view
import groovy.swing.SwingBuilder
import static javax.swing.JFrame.EXIT_ON_CLOSE
import static javax.swing.WindowConstants.EXIT_ON_CLOSE

class GUI {

    def GUI(){
        SwingBuilder.build {
            frame(title:"Analizador LL1",show:true,bounds:[200,100,600,600],
                  defaultCloseOperation: EXIT_ON_CLOSE){
                gridLayout(cols:3,rows:3)

            }
        }
    }
}
