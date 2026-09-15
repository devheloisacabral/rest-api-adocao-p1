package br.com.adocao.animal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "animais")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String especie;

    private String raca;

    @Column(nullable = false)
    private Integer idade;

    @Column(nullable = false)
    private String sexo;

    @Column(nullable = false)
    private String porte;

    @Lob
    private String descricao;

    @Column(name = "disponivel_para_adocao", nullable = false)
    private Boolean disponivelParaAdocao;

    protected Animal() {
    }

    public Animal(String nome, String especie, String raca, Integer idade, String sexo, String porte,
                  String descricao, Boolean disponivelParaAdocao) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.sexo = sexo;
        this.porte = porte;
        this.descricao = descricao;
        this.disponivelParaAdocao = disponivelParaAdocao;
    }

    public void atualizar(String nome, String especie, String raca, Integer idade, String sexo, String porte,
                          String descricao, Boolean disponivelParaAdocao) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.sexo = sexo;
        this.porte = porte;
        this.descricao = descricao;
        this.disponivelParaAdocao = disponivelParaAdocao;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEspecie() {
        return especie;
    }

    public String getRaca() {
        return raca;
    }

    public Integer getIdade() {
        return idade;
    }

    public String getSexo() {
        return sexo;
    }

    public String getPorte() {
        return porte;
    }

    public String getDescricao() {
        return descricao;
    }

    public Boolean getDisponivelParaAdocao() {
        return disponivelParaAdocao;
    }
}
