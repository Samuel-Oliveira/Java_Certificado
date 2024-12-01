# Java_Certificado [![MIT License](https://img.shields.io/github/license/Samuel-Oliveira/Java_Certificado.svg)](https://github.com/Samuel-Oliveira/Java_Certificado/blob/master/LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/br.com.swconsultoria/java_certificado.svg?label=Maven%20Central)](https://search.maven.org/artifact/br.com.swconsultoria/java_certificado/3.6/jar)

Projeto Java de Gerenciamentos de Certificado Digital

## Dúvidas, Sugestões ou Consultoria
[![Java Brasil](https://discordapp.com/api/guilds/519583346066587676/widget.png?style=banner2)](https://discord.gg/ZXpqnaV)

## Gostou do Projeto? Dê sua colaboração pelo Pix: 01713390108
<img src="https://swconsultoria.com.br/pix.png" width="200">

Para Iniciar : 
- Caso use External Jar, baixe a ultima versão em [Releases](https://github.com/Samuel-Oliveira/Java_Certificado/releases) e o adicione às bibliotecas de Seu Projeto.

- Caso use Maven :
```
<dependency>
    <groupId>br.com.swconsultoria</groupId>
    <artifactId>java_certificado</artifactId>
    <version>3.6</version>
</dependency>
```

Veja a [Wiki](https://Samuel-Oliveira.github.io/Java_Certificado/), para ter um Tutorial Completo.

________________________________________________________________________________________________
# Historico de Versões

## v3.8 - 30/11/2024
- Atualizado CACERT
- Adicionado modo Multithreading

## v3.7 - 15/10/2024
- Atualizado CACERT

## v3.6 - 08/06/2024
- Gerado Novo Cacert

## v3.5 - 19/04/2024
- Gerado Novo Cacert

## v3.4 - 25/03/2024
- Correcao de Null Pointer Exception ao lista Repositorio Windows.
- Adicionado GitActions para gerar o Jar

## v3.3 - 20/03/2024
- Refatoracao do Metodo de extracao CNPJ e CPF e geracao de cacert

## v3.2 - 05/03/2024
- Ajustes Metodo de extracao CNPJ e CPF

## v3.1 - 21/02/2024
- Corrigido fechamento do Socket
- Melhorado logs API

## v3.0 - 18/02/2024
- Gerado novo Cacert
- Refatoracao Geral do Codigo
- Removido dependencia Bouncycastle
- Refeito toda parte de Certificado A3 (Se vc trabalha com A3 Veja as mudancas: https://github.com/Samuel-Oliveira/Java_Certificado/wiki/3%E2%80%90Certificado-A3)
- Adicionado compatibilidade com todas as versoes LTS - 8, 11, 17 e 21

## v2.13 - 26/11/2023
- Gerado novo Cacert

## v2.12 - 25/09/2023
- Correçoes Certificados A3

## v2.11 - 21/08/2023
- Gerado novo Cacert

## v2.10 - 05/05/2023
- Gerado novo Cacert
- Adicionado numero de serie ao objeto certificado
- Melhorias Exceptions

## v2.9 - 21/01/2023
- Gerado novo Cacert
- Atualizado Versão BouncyCastle para previnir vunerabilidades
- Adicionado ToString no Certificado

## v2.8 - 22/09/2021
- Gerado novo Cacert

## v2.7 - 01/08/2021
- Gerado novo Cacert
- Retornado Versão bc-prov para 16
- Adicionado possibilidade de buscar Certificado pela raiz do CNPJ

## v2.6 - 28/06/2021
- Gerado novo Cacert
- Alterado Versão bc-prov para evitar conflitos com outras Bibliotecas
- Adicionado possibilidade de buscar Certificado pela raiz do CNPJ

## v2.5 - 04/04/2021
- Gerado novo Cacert
- Refatoração nos Métodos de Respositorio (Windows/Mac)

## v2.4 - 01/04/2021
- Adicionado Cacert e Cacert Util a este projeto, agora este projeto que irá gerenciar os Cacerts.
- Adicionado propriedade de DataHora de vencimento
- Removido Objetos Depreciados
- Melhoria dos Logs
- Adicionado Tipo Certificado A3 EnterSafe

## v2.3 - 20/07/2019
- Corrigido erro Unknow Certificate

## v2.2 - 26/05/2019
- Refatoração de Código
- Certificado.tipo Depreciado
- Criado Certificado.tipoCertificado
- Incluido Testes Unitários

## v2.1 - 24/04/2019
- Adicionado Propriedade CPF/CNPJ ao certificado
- CertificadoService.getCertificadoByCnpj Depreciada.
- CertificadoService.getCertificadoByCpf Depreciada.
- Criado CertificadoService.getCertificadoByCnpjCpf
- Adicionado Compatibilidade para MAC

## v2.0 - 08/01/2018
- Adicionado Ao Maven Central
- Alterado nome de Packages
- Limpeza no Projeto

## v1.9 - 20/06/2018
- Adicionado opção de pegar o Certificado (Windows) pelo Cpf

## v1.8 - 09/05/2018
- Adicionado opção de pegar o Certificado (Windows) pelo Cnpj

## v1.7 - 04/04/2018
- Adicionado Slot na Configuração do A3 para 2 A3 na Mesma Maquina. 

## v1.6 - 11/03/2018
- Corrigido erro de Unknow CA

## v1.5 - 18/09/2017
- Adicionado Parametro para Alterar Protocolo SSL
- Corrigido erro de A3.
- Adicionado A3 com COnfiguração por Alias
- Adicionado Metodo que lista A3

## v1.4 - 05/07/2017
- Alterado COnfigurações Para Nfe 4.00

## v1.3 - 04/07/2017
- Adicionado opção de registrar certificados atraves do properties

## v1.2 - 20/06/2017
- Correções do Socket Dinamico para Aceitar o WS HOmologação Goias.

## v1.1 - 08/06/2017
- Refeitas Configurações do Socket Dinamico
- Adicionacio Para aceitar Arquivos PFX como Byte[]

## v1.0 - 29/05/2017
- Versão Inicial Do Sistema
