package mx.ipn.analizadorSintactico.controller

import mx.ipn.analizadorSintactico.domain.Lista
import mx.ipn.analizadorSintactico.service.AnalizadorSintacticoService
import mx.ipn.analizadorSintactico.utils.First
import mx.ipn.analizadorSintactico.utils.Follow

/**
 * Author: Gamaliel Jiménez
 */
class AnalizadorSintacticoController {

    def analizadorSintacticoService
    def mapTerminalToken

    def AnalizadorSintacticoController(def mapTerminalToken){
        analizadorSintacticoService = new AnalizadorSintacticoService()
        this.mapTerminalToken = mapTerminalToken
    }

    def crearListaGramaticas(){
        def stringBuilder = new StringBuilder()

        analizadorSintacticoService.linesFromFile().each{
            stringBuilder.append(it)
        }

        def gramaticas = stringBuilder.toString()

        /*Se crea el alfabeto verificando cada carácter del archivo*/
        analizadorSintacticoService.crearAlfabeto(gramaticas)

        /*Lista de listas*/
        def list = analizadorSintacticoService.createListFromProduction(gramaticas)

        def mapOfLists = [:]

        analizadorSintacticoService.createAMapOfLists(mapOfLists,list)

        mapOfLists

    }

    def calcularFirst(def mapOfLists){

        def first = new First(mapOfLists)

        /*Calculo de todos los first*/
        mapOfLists.each{
            first.getFirstOfNodo(it.value)
        }

        first
    }

    def calcularFollow(def first,mapOfLists){

        def mapOfFollow = [:]
        def noTerminales = analizadorSintacticoService.getNoTerminalesConDerivacionEpsilon(mapOfLists)
        def follow = new Follow(mapOfLists,first.mapOfFirst)

        noTerminales.each{ noTerminal ->
            def auxmap = [:]

            follow.getFollowOfNodo(mapOfLists.get(noTerminal)).each{
                auxmap.put(it,"ε")
            }

            mapOfFollow.(noTerminal.toString()) = auxmap
        }

        mapOfFollow
    }

    def obtenerTerminales(def mapOfLists){
        def terminales = []

        mapOfLists.each{
            terminales.addAll(Lista.terminalesSub(it.value.sig))
        }

        terminales.unique()
    }


    def analizaCadena(def first,def follow,def terminales,def lexemasAndTokens){

        def datos = []
        def listData = []
        def auxString = ""

        def inputArray = []
        def cadena = []
        def subcadena = ""

        lexemasAndTokens.each{
            cadena.add(it.get(0))
            inputArray.add(it.get(1))
        }


        subcadena = concat(cadena)

        Stack<String> pila = new Stack<String>()
        pila.push('$')
        pila.push(first.iterator().next().key)

        listData.add('$')
        listData.add("")
        listData.add("push (${first.iterator().next().key})")

        datos.add(listData)

        def pointer = 0
        def a = inputArray.get(pointer)
        def X = pila.peek()

        while(!X.equals('$')){
            listData = []
            auxString = ""

            if(X.equals(mapTerminalToken.get(a))){
                pila.elements().each{
                    auxString+=it
                }
                listData.add(auxString)
                listData.add(subcadena)
                listData.add("pop (${pila.peek()})")

                pila.pop()
                //cadena = cadena.substring(a.length(),cadena.length())
                pointer++;
                a = inputArray.get(pointer)

                subcadena = subcadena.substring(cadena.get(pointer-1).length(),subcadena.length())

            }
            else if(first.get(X)?.get(mapTerminalToken.get(a))){
                def items = analizadorSintacticoService.getItemsOfProd(first.get(X)?.get(mapTerminalToken.get(a)))

                pila.elements().each{
                    auxString+=it
                }
                listData.add(auxString)
                listData.add(subcadena)
                auxString = ""

                items.each{
                    auxString+=it
                }

                listData.add(auxString)

                items = items.reverse()

                pila.pop()

                items.each{
                    pila.push(it)
                }
            }
            else if(follow.get(X)?.get(mapTerminalToken.get(a))){
                pila.elements().each{
                    auxString+=it
                }

                listData.add(auxString)
                listData.add(subcadena)
                listData.add(follow.get(X)?.get(mapTerminalToken.get(a)))

                pila.pop()
            }
            else{
                return datos
            }

            datos.add(listData)

            X = pila.peek()
        }


        listData = []
        listData.add('$')
        listData.add("")
        listData.add("ACCEPTED")

        datos.add(listData)
        datos
    }


    def isTerminal(String cadena,def terminales){
        if(terminales.contains(cadena))
            return true
        false
    }

    def concat(inputArray){
        def cadena = new StringBuilder()

        inputArray.each{
            cadena.append(it)
        }

        cadena.toString()
    }

}
