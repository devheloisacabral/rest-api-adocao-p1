package br.com.adocao.animal;

public record AnimalResponse(
        Long id,
        String nome,
        String especie,
        String raca,
        Integer idade,
        String sexo,
        String porte,
        String descricao,
        Boolean disponivelParaAdocao
) {
    public static AnimalResponse from(Animal animal) {
        return new AnimalResponse(
                animal.getId(),
                animal.getNome(),
                animal.getEspecie(),
                animal.getRaca(),
                animal.getIdade(),
                animal.getSexo(),
                animal.getPorte(),
                animal.getDescricao(),
                animal.getDisponivelParaAdocao()
        );
    }
}
