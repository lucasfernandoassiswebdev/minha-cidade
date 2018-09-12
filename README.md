# Minha Cidade - Descrição
Deverá possuir os seguintes módulos obrigatórios para controle como: Cadastro de

Cidadão: É obrigatório informar nome, CPF, e-mail, usuário e senha. O campo data de nascimento é opcional. Informando os campos obrigatórios o cadastro é realizado. Cadastro de Ocorrência: É obrigatório informar, endereço, número, categoria e descrição. O usuário possui a opção de inserir uma imagem referente à ocorrência, se o mesmo achar necessário. Após o cadastro a ocorrência é enviada a um atendente que irá analisar se a mesma é válida. O endereço necessita da API Google Places para pesquisar os logradouros.

Deverá disponibilizar Listagem de Ocorrências do Cidadão: O cidadão tem a opção de visualizar suas ocorrências cadastradas, onde o sistema apresenta uma listagem contendo data, código, categoria, situação, botão de visualizar detalhado e avaliação do atendimento. Usando o botão visualizar, o usuário consegue ver todos os dados informados na
ocorrência, inclusive a imagem.

Permitir alteração de dados do cidadão: O cidadão tem a opção de alterar seus dados de cadastro, podendo mudar o nome, data de nascimento e e-mail.

Permitir mapeamento: O sistema apresenta um mapa contendo as ocorrências cadastras, separadas em andamento (ponto vermelho) e resolvidas (ponto verde).

Permitir estatísticas: O sistema apresenta em forma de números quantas ocorrências foram feitas, cidadãos cadastrados, problemas resolvidos e atendimentos qualificados como bom.

Permitir avaliação de Ocorrências Recebidas: O sistema deve ter um atendente para avaliar as ocorrências, se as mesmas vão para analise ou são invalidas. A ocorrência sendo válida, o responsável pela categoria passará a dar andamento no atendimento, o cidadão receberá um e-mail informando a situação de sua ocorrência. O atendente

visualiza as ocorrências em uma listagem, podendo selecionar a que deseja avaliar.

Permitir análise de Ocorrências: O sistema deve conter um usuário para administrar cada categoria de ocorrência, o mesmo irá analisar cada ocorrência, podendo resolvê-la ou não. O usuário optando por resolver a ocorrência deve selecionar um funcionário responsável por executar a resolução. Após a resolução da ocorrência o usuário deve informar se a ocorrência foi ou não encerrada, finalizando o atendimento. E a cada mudança de situação da ocorrência o cidadão deve ser informado via e-mail em que andamento se encontra seu atendimento.

Permitir o cadastro de Funcionários: É obrigatório informar nome, CPF, cargo e a categoria que o funcionário pertence. Se o cargo do usuário for atendente ou administrador é necessários preencher os campos de usuário, e-mail e senha. O usuário com cargo de administrador só pode ser responsável por uma categoria.

Permitir cadastro de Categorias/Departamentos: É obrigatório informar a descrição da categoria. O cadastro só possui o campo de descrição.

Permitir relatório: O sistema possui opção de gerar relatório por período, categoria ou situação. O relatório apresenta os dados das ocorrências, tais como, código, data, situação, endereço e descrição. As ocorrências são agrupadas por categoria, apresentando um totalizador.

# DER 
Acessar o link para visualizar e editar a diagramação do projeto.
https://drive.google.com/file/d/1DIMQEsAS-FCCo3QlzPBqbelMCapehogk/view?usp=sharing

# Diagrama de Processos
Diagrama de processos criado com a ferramenta HEFLO, que permite o compartilhamento para edição entre a equipe.

![alt text](https://github.com/vitoreddydata/minha-cidade/blob/master/Minha%20CidadePreview.png)


