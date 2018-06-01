# Desafío de Mercado Libre

Autor: Pablo Giudice

[![Build Status](https://travis-ci.org/pgiu/mutante.svg?branch=master)](https://travis-ci.org/pgiu/mutante)


## Objetivo

El objetivo de la aplicación es detectar si cierta matriz de ADN corresponde al de un humano o al de un mutante. 

Una matriz de ADN tiene una forma como la siguiente: 

````
CGTG
GTAG
GGTT
ATTT
````

Se dice que el ADN es de un mutante si posee más de una secuencia de 4 caracteres iguales (A, C, G, T).



## Descripción

El proyecto contiene un servidor web REST basado en Spring Boot. 

Los mensajes que procesa son dos: 

- POST /mutant , que recibe un json como el siguiente

   ````
   {
       "dna" : ["AAAA", "ATCG", "GTCA", "GGGG"]
   }
   ````

- GET /stats , que devuelve un json de la forma: 

  ````
  {
      "count_mutant_dna":40, 
      "count_human_dna":100: 
      "ratio":0.4
  }
  ````

  



## Como probarlo

Hay una versión de prueba alojada en AWS. 

La URL es http://mutanteapp-env.ypighmykyw.us-west-1.elasticbeanstalk.com

Configuración: 

- Elastic Beanstalk
  - t1.micro
  - Sin load balancer (no está en el Free Tier :( 
- Database: 
  - MySQL en db.t2.micro (Amazon RDS)

## Escalabilidad

Hice pruebas usando www.loader.io y logré servir hasta 1000 peticiones por minuto. Al intentar elevar el número a 10k peticiones, el numero de timeouts de red hace que los tests falle. La forma de solucionar esto es:

- incrementar el poder de cómputo de las [instancias](https://aws.amazon.com/ec2/instance-types/) (large/xlarge) 
- agregar más instancias (y un load balancer)
- crear workers para la escritura a la base de datos.



## Compilar el código fuente 

En este repo se encuentra el código fuente del proyecto. Para compilarlo y correr los tests automáticos, ejecutar las siguientes líneas.

````
mvn 
cd target
java -jar mutante-app-0.1.0.jar
````



## Consideraciones

- El tamaño máximo de la matriz de ADN es 1000x1000.

- El algoritmo de búsqueda es simple pero permite paralelizar fácilmente. Pueden usarse threds distintos para hacer búsquedas en direcciones distintas. Esto permitiría multiplicar por 6 la performance. 

- El algoritmo no hace copias de la matriz de entrada, por lo que es óptimo desde el punto de vista de memoria. 

- En el peor caso (no hay cadenas mutantes) hay que recorrer la totalidad de la matriz 4 veces (una por cada dirección: horizontal, vertical, diagonal arriba-abajo, diagonal abajo-arriba). 

  Por lo tanto, desde el punto de vista de tiempo de ejecución, es lineal con el tamaño de la matriz de entrada (N = nxn).

- Para casos más extremos, podría considerarse asignar un thread a cada fila, columna o diagonal. Esto permitiría, en teoría, buscar N veces más rápido, pero no tiene en cuenta el costo de iniciar N threads.

  

  