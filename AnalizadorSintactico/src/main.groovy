import mx.ipn.analizadorSintactico.controller.AnalizadorSintacticoController
import mx.ipn.analizadorSintactico.view.GUI

/**
 * User: Gamaliel Jiménez
 * Date: 15/03/14
 */
def analizadorSintacticoController = new AnalizadorSintacticoController()
def mapOfLists =  analizadorSintacticoController.crearListaGramaticas()
def terminales = analizadorSintacticoController.obtenerTerminales(mapOfLists)

def first = analizadorSintacticoController.calcularFirst(mapOfLists)
def follow = analizadorSintacticoController.calcularFollow(first,mapOfLists)


def ventana = new GUI(terminales,first.mapOfFirst,follow)

