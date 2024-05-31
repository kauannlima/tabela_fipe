package br.com.klima.tabelafipe.tabela_fipe.principal;

import br.com.klima.tabelafipe.tabela_fipe.model.DadosMarcas;
import br.com.klima.tabelafipe.tabela_fipe.model.DadosModelos;
import br.com.klima.tabelafipe.tabela_fipe.model.DadosVeiculo;
import br.com.klima.tabelafipe.tabela_fipe.service.ConsumoAPI;
import br.com.klima.tabelafipe.tabela_fipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();

    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    private String endereco;

    public void exibeMenu() {
        String menu = "Opções\nCarros\nMotos\nCaminhões\nDigite uma das opções para consultar: ";
        System.out.println(menu);

        var opcoes = leitura.nextLine();

        if (opcoes.toLowerCase().contains("carr")) {
            endereco = URL_BASE + "carros/marcas";
        } else if (opcoes.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        } else if (opcoes.toLowerCase().contains("cami")) {
            endereco = URL_BASE + "caminhoes/marcas";
        }
        var json = consumoAPI.obterDados(endereco);
        System.out.println(json);
        var marcas = conversor.obterLista(json, DadosMarcas.class);
        marcas.stream().sorted(Comparator.comparing(DadosMarcas::codigo)).forEach(System.out::println);


        System.out.println("Informe o código da marca para consulta: ");

        var codigoMarcas = leitura.nextLine();
        endereco = endereco + "/" + codigoMarcas + "/modelos";
        json = consumoAPI.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, DadosModelos.class);

        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream().sorted(Comparator.comparing(DadosMarcas::codigo)).forEach(System.out::println);

        System.out.println("Informe um trco do veiculo a ser buscado: ");
        var nomeVeiculo = leitura.nextLine();

        List<DadosMarcas> modelosFiltrados = modeloLista.modelos().stream().filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase())).collect(Collectors.toList());

        System.out.println("\nModelos filtardos");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite por favor o código do modelo: ");
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumoAPI.obterDados(endereco);
        List<DadosMarcas> anos = conversor.obterLista(json, DadosMarcas.class);
        List<DadosVeiculo> veiculos = new ArrayList<>();

        for (int i =0; i < anos.size();i++){
            var enderecoAnos = endereco + "/"+anos.get(i).codigo();
            json = consumoAPI.obterDados(enderecoAnos);
            DadosVeiculo veiculo= conversor.obterDados(json, DadosVeiculo.class);
            veiculos.add(veiculo);
        }

       veiculos.forEach(System.out::println);
    }
}
