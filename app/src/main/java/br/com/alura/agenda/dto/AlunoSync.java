package br.com.alura.agenda.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import br.com.alura.agenda.modelo.Aluno;

public class AlunoSync {
//    private List<Aluno> alunos;  //ouu
    @JsonProperty("alunos")
    private List<Aluno> alunoList;
    private String momentoDaUltimaModificacao;

    public List<Aluno> getAlunoList() {
        return alunoList;
    }

    public String getMomentoDaUltimaModificacao() {
        return momentoDaUltimaModificacao;
    }
}
