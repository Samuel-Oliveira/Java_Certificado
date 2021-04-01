# Java_Certificado [![MIT License](https://img.shields.io/github/license/Samuel-Oliveira/Java_Certificado.svg)](https://github.com/Samuel-Oliveira/Java_Certificado/blob/master/LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/br.com.swconsultoria/java_certificado.svg?label=Maven%20Central)](https://search.maven.org/artifact/br.com.swconsultoria/java_certificado/2.4/jar) [![Build Status](https://travis-ci.org/Samuel-Oliveira/Java_Certificado.svg?branch=master)](https://travis-ci.org/Samuel-Oliveira/Java_Certificado) [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Samuel-Oliveira/Java_Certificado.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Samuel-Oliveira/Java_Certificado/context:java) [![Total alerts](https://img.shields.io/lgtm/alerts/g/Samuel-Oliveira/Java_Certificado.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Samuel-Oliveira/Java_Certificado/alerts/)

Projeto Java de Gerenciamentos de Certificado Digital

## Dúvidas, Sugestões ou Consultoria
Entre no Discord do Projeto: https://discord.gg/ZXpqnaV

## Gostou do Projeto? Dê sua colaboração pelo Pix: 01713390108 <img src="https://swconsultoria.com.br/pix.png" width="150">

Para Iniciar : 
- Caso use Libs baixe o java-certificado-2.4.jar (https://github.com/Samuel-Oliveira/Java_Certificado/raw/master/java-certificado-2.4.jar) e o adicione às bibliotecas de Seu Projeto.

- Caso use Maven :
```
<dependency>
    <groupId>br.com.swconsultoria</groupId>
    <artifactId>java_certificado</artifactId>
    <version>2.4</version>
</dependency>
```

Veja a Wiki https://github.com/Samuel-Oliveira/Java_Certificado/wiki, para ter um Tutorial Completo.

________________________________________________________________________________________________
# Historico de Versões

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