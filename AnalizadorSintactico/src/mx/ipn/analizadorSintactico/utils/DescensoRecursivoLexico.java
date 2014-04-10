package mx.ipn.analizadorSintactico.utils;

import mx.ipn.analizadorSintactico.domain.ClaseLexica;
import mx.ipn.analizadorSintactico.domain.AFN;
import mx.ipn.analizadorSintactico.domain.TokenCL;

/**
 * Created with IntelliJ IDEA.
 * User: gamaliel
 * Date: 10/04/14
 * Time: 05:57
 * To change this template use File | Settings | File Templates.
 */
public class DescensoRecursivoLexico {
    private ClaseLexica lexic;

    /*Constructor*/
    public DescensoRecursivoLexico(){}

    /*Inyección por constructor*/
    public DescensoRecursivoLexico(ClaseLexica lexic){
        this.lexic = lexic;
    }

    /*Métodos para el descenso recursivo*/
    boolean E(AFN f1){
        if(T(f1))
            if(Ep(f1))
                return true;
        return false;
    }

    boolean Ep(AFN f1){
        Integer tok;
        tok =lexic.yylex();

        if(tok == TokenCL.OR){
            System.out.println("OR");
            AFN f2 = new AFN();
            if(T(f2)){
                f1.unir(f2);
                if(Ep(f1))
                    return true;
            }
            return false;
        }

        lexic.regresarToken();
        return true;
    }

    boolean T(AFN f1){
        if(C(f1))
            if(Tp(f1))
                return true;
        return false;
    }

    boolean Tp(AFN f1){
        Integer tok;
        tok = lexic.yylex();

        if(tok == TokenCL.CONC){
            System.out.println("CONC");
            AFN f2 = new AFN();
            if(C(f2)){
                f1.conc(f2);
                if(Tp(f1))
                    return true;
            }
            return false;
        }

        lexic.regresarToken();
        return true;
    }

    boolean C(AFN f1){
        if(F(f1))
            if(Cp(f1))
                return true;

        return false;
    }

    boolean Cp(AFN f1){
        Integer tok;
        tok = lexic.yylex();

        switch(tok){
            case TokenCL.CERR_POS:
                f1.cerrPos();
                break;
            case TokenCL.CERR_ESTR:
                System.out.println("Estrella");
                f1.cerrEstr();
                break;
            case TokenCL.OPC:
                f1.opc();
                break;
            default:
                lexic.regresarToken();
                return true;
        }

        return Cp(f1);
    }

    boolean F(AFN f1){
        Integer tok;
        tok = lexic.yylex();
        if(tok == TokenCL.PAR_I){
            if(E(f1)){
                tok = lexic.yylex();
                if(tok == TokenCL.PAR_D)
                    return true;
            }
            return false;
        }
        if(tok == TokenCL.SIMB){
            System.out.println("SIMBOLO");
            f1.createWithASymbol(lexic.getLexema());
            return true;
        }
        return false;
    }
}
