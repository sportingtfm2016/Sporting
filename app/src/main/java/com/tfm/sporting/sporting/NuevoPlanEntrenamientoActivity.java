package com.tfm.sporting.sporting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fuzzylite.Engine;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.defuzzifier.WeightedAverage;
import com.fuzzylite.norm.s.AlgebraicSum;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Trapezoid;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

public class NuevoPlanEntrenamientoActivity extends AppCompatActivity {

    private Button btnGenerarPlan;
    private EditText etKmObjetivo;

    InputVariable Edad = new InputVariable();
    InputVariable Actividad = new InputVariable();
    InputVariable Objetivo = new InputVariable();

    final OutputVariable Semanas = new OutputVariable();
    OutputVariable Dias = new OutputVariable();
    OutputVariable km = new OutputVariable();
    Engine engine = new Engine();

    InputVariable inputVariable = new InputVariable();
    OutputVariable outputVariable = new OutputVariable();
    RuleBlock ruleBlock = new RuleBlock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_plan_entrenamiento);


        btnGenerarPlan = (Button) findViewById(R.id.btnGenerarPlan);
        etKmObjetivo = (EditText) findViewById(R.id.etKmObjetivo);

        //TODO Meter servicio obtener edad y actividad fisica

        engine.setName("PlanesEntrenamiento");

        //Entradas

        Edad.setEnabled(true);
        Edad.setName("Edad");
        Edad.setRange(8.000, 50.000);
        Edad.addTerm(new Trapezoid("Joven", 8.000, 8.000, 12.000, 15.000));
        Edad.addTerm(new Triangle("Adolescente", 13.000, 22.289, 30.000));
        Edad.addTerm(new Triangle("Adulto", 27.000, 33.885, 40.000));
        Edad.addTerm(new Trapezoid("Mayor", 38.000, 45.000, 50.000, 50.000));
        engine.addInputVariable(Edad);


        Actividad.setEnabled(true);
        Actividad.setName("Actividad");
        Actividad.setRange(0.000, 3.000);
        Actividad.addTerm(new Triangle("Baja", 0.000, 0.000, 1.000));
        Actividad.addTerm(new Triangle("Media", 0.486, 1.500, 2.500));
        Actividad.addTerm(new Triangle("Alta", 1.992, 3.000, 4.200));
        engine.addInputVariable(Actividad);


        Objetivo.setEnabled(true);
        Objetivo.setName("Objetivo");
        Objetivo.setRange(0.000, 100.000);
        Objetivo.addTerm(new Trapezoid("Bajo", 0.000, 0.000, 10.000, 20.000));
        Objetivo.addTerm(new Trapezoid("Medio", 10.000, 20.000, 40.000, 50.000));
        Objetivo.addTerm(new Trapezoid("Resistencia", 40.000, 60.000, 100.000, 100.000));
        engine.addInputVariable(Objetivo);


        //Salidas

        Semanas.setEnabled(true);
        Semanas.setName("Semanas");
        Semanas.setRange(1.000, 4.000);
        Semanas.fuzzyOutput().setAccumulation(new Maximum());
        Semanas.fuzzyOutput().setAccumulation(new AlgebraicSum());
        Semanas.setDefuzzifier(new WeightedAverage());
        Semanas.setDefuzzifier(new Centroid(200));
        Semanas.setDefaultValue(Double.NaN);
        Semanas.setLockValidOutput(false);
        Semanas.setLockOutputRange(false);
        Semanas.addTerm(new Triangle("Corto", -0.200, 1.000, 2.200));
        Semanas.addTerm(new Triangle("Medio", 1.300, 2.500, 3.700));
        Semanas.addTerm(new Triangle("Largo", 2.800, 4.000, 5.200));
        engine.addOutputVariable(Semanas);

        Dias.setEnabled(true);
        Dias.setName("Dias");
        Dias.setRange(1.000, 7.000);
        Dias.fuzzyOutput().setAccumulation(new Maximum());
        Dias.fuzzyOutput().setAccumulation(new AlgebraicSum());
        Dias.setDefuzzifier(new WeightedAverage());
        Dias.setDefuzzifier(new Centroid(200));
        Dias.setDefaultValue(Double.NaN);
        Dias.setLockValidOutput(false);
        Dias.setLockOutputRange(false);
        Dias.addTerm(new Trapezoid("Suave", -1.160, 0.760, 1.530, 3.028));
        Dias.addTerm(new Trapezoid("Normal", 1.840, 3.160, 3.970, 4.998));
        Dias.addTerm(new Trapezoid("Intenso", 4.010, 5.572, 7.010, 7.010));
        engine.addOutputVariable(Dias);

        km.setEnabled(true);
        km.setName("km");
        km.setRange(0.000, 100.000);
        km.fuzzyOutput().setAccumulation(new Maximum());
        //km.fuzzyOutput().setAccumulation(new AlgebraicSum());
        km.setDefuzzifier(new WeightedAverage());
        km.setDefuzzifier(new Centroid(200));
        km.setDefaultValue(Double.NaN);
        km.setLockValidOutput(false);
        km.setLockOutputRange(false);
        km.addTerm(new Trapezoid("Bajo", 0.000, 0.000, 10.000, 20.000));
        km.addTerm(new Trapezoid("Normal", 10.000, 20.000, 40.000, 50.000));
        km.addTerm(new Trapezoid("Resistencia", 40.000, 60.000, 100.000, 100.000));
        engine.addOutputVariable(km);


        //Reglas

        ruleBlock.setEnabled(true);
        ruleBlock.setName("");
        ruleBlock.setConjunction(new Minimum());
        ruleBlock.setDisjunction(new Maximum());
        ruleBlock.setActivation(new Minimum());
        ruleBlock.addRule(Rule.parse("if Edad is Joven and Actividad is Baja and Objetivo is Bajo then Semanas is Medio and Dias is Normal and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Joven and Actividad is Baja and Objetivo is Medio then Semanas is Largo and Dias is Normal and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Joven and Actividad is Baja and Objetivo is Resistencia then Semanas is Largo and Dias is Intenso and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Joven and Actividad is Media and Objetivo is Bajo then Semanas is Corto and Dias is Suave and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Joven and Actividad is Media and Objetivo is Medio then Semanas is Medio and Dias is Normal and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Joven and Actividad is Media and Objetivo is Resistencia then Semanas is Medio and Dias is Normal and km is Resistencia", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Joven and Actividad is Alta and Objetivo is Bajo then Semanas is Corto and Dias is Suave and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Joven and Actividad is Alta and Objetivo is Medio then Semanas is Corto and Dias is Suave and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Joven and Actividad is Alta and Objetivo is Resistencia then Semanas is Medio and Dias is Suave and km is Resistencia", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adolescente and Actividad is Baja and Objetivo is Bajo then Semanas is Corto and Dias is Suave and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adolescente and Actividad is Baja and Objetivo is Medio then Semanas is Medio and Dias is Normal and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adolescente and Actividad is Baja and Objetivo is Resistencia then Semanas is Largo and Dias is Normal and km is Resistencia", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adolescente and Actividad is Media and Objetivo is Bajo then Semanas is Corto and Dias is Suave and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adolescente and Actividad is Media and Objetivo is Medio then Semanas is Medio and Dias is Normal and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adolescente and Actividad is Media and Objetivo is Resistencia then Semanas is Medio and Dias is Normal and km is Resistencia", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adolescente and Actividad is Alta and Objetivo is Bajo then Semanas is Corto and Dias is Suave and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adolescente and Actividad is Alta and Objetivo is Medio then Semanas is Corto and Dias is Normal and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adolescente and Actividad is Alta and Objetivo is Resistencia then Semanas is Corto and Dias is Suave and km is Resistencia", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adulto and Actividad is Baja and Objetivo is Bajo then Semanas is Corto and Dias is Normal and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adulto and Actividad is Baja and Objetivo is Medio then Semanas is Medio and Dias is Normal and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adulto and Actividad is Baja and Objetivo is Resistencia then Semanas is Largo and Dias is Intenso and km is Resistencia", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adulto and Actividad is Media and Objetivo is Bajo then Semanas is Corto and Dias is Suave and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adulto and Actividad is Media and Objetivo is Medio then Semanas is Medio and Dias is Normal and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adulto and Actividad is Media and Objetivo is Resistencia then Semanas is Largo and Dias is Normal and km is Resistencia", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adulto and Actividad is Alta and Objetivo is Bajo then Semanas is Corto and Dias is Suave and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adulto and Actividad is Alta and Objetivo is Medio then Semanas is Medio and Dias is Suave and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Adulto and Actividad is Alta and Objetivo is Resistencia then Semanas is Largo and Dias is Normal and km is Resistencia", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Mayor and Actividad is Baja and Objetivo is Bajo then Semanas is Medio and Dias is Normal and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Mayor and Actividad is Baja and Objetivo is Medio then Semanas is Largo and Dias is Normal and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Mayor and Actividad is Baja and Objetivo is Resistencia then Semanas is Largo and Dias is Intenso and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Mayor and Actividad is Media and Objetivo is Bajo then Semanas is Corto and Dias is Normal and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Mayor and Actividad is Media and Objetivo is Medio then Semanas is Medio and Dias is Intenso and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Mayor and Actividad is Media and Objetivo is Resistencia then Semanas is Medio and Dias is Intenso and km is Resistencia", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Mayor and Actividad is Alta and Objetivo is Bajo then Semanas is Corto and Dias is Normal and km is Bajo", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Mayor and Actividad is Alta and Objetivo is Medio then Semanas is Medio and Dias is Normal and km is Normal", engine));
        ruleBlock.addRule(Rule.parse("if Edad is Mayor and Actividad is Alta and Objetivo is Resistencia then Semanas is Medio and Dias is Normal and km is Resistencia", engine));
        engine.addRuleBlock(ruleBlock);



        //Tarea boton Login
        btnGenerarPlan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //engine.configure("", "", "Minimum", "Maximum", "Centroid");
                //TODO obtener Edad y Nivel
                engine.setInputValue("Edad", 42);
                engine.setInputValue("Actividad", 2);
                engine.setInputValue("Objetivo", Integer.parseInt(etKmObjetivo.getText().toString()));

                engine.process();

                double var = engine.getOutputVariable("km").defuzzify();
                String km = String.valueOf(var);
            }
        });
    }
}
