var app = angular.module('myApp', []);
app.controller('MutanteController', function ($scope, $http, $location) {

    // Servidor local
    //$scope.server_url = "http://localhost:8080";
    //$scope.server_url = "http://mutanteapp-env.ypighmykyw.us-west-1.elasticbeanstalk.com";
    $scope.server_url = $location.url();

    $scope.cont_mutantes = 0;
    $scope.cont_humanos = 0;
    $scope.ratio = 0;

    $scope.isMutantResult = null;

    $scope.last_transaction_time_is_mutant = null;
    $scope.last_transaction_time_stats = null;

    $scope.randomSize = 8;

    $scope.sample_dna_human = ["ACGG", "TCGA", "AGCC", "TTGC"];
    $scope.sample_dna_mutant = ["GGGG", "AAAA", "TGCC", "AGGC"];
    $scope.sample_dna_mutant_2 = ["CCGG", "CCGG", "CGCG", "CGTG"];
    $scope.current_dna = $scope.sample_dna_human;

    $scope.getDate = function () {
        return new Date().toLocaleDateString() + " " + new Date().toTimeString();
    }

    $scope.isMutant = function () {
        console.log("Sending DNA: ");
        console.log($scope.current_dna);

        $http.post($scope.server_url + "/mutant", {
            "dna": $scope.current_dna
        }).then(
                function (response) {
                    $scope.isMutantResult = "mutante";
                    $scope.last_transaction_time_is_mutant = $scope.getDate();
                    console.log("Mutante: " + $scope.isMutantResult);
                    // Actualizar estadísticas
                    $scope.getStats();
                },
                function (response) {
                    if (response.status === 403) {
                        $scope.isMutantResult = "humano";
                        console.log("Mutante: " + $scope.isMutantResult);
                    } else {
                        $scope.isMutantResult = "error";
                        console.log("Error in response from server: " + JSON.stringify(response));
                    }
                    $scope.last_transaction_time_is_mutant = $scope.getDate();
                    // Actualizar estadísticas
                    $scope.getStats();
                }
        );
    }

    $scope.setCurrentDNA = function (dna) {
        $scope.current_dna = dna;
    }

    $scope.getStats = function () {

        $http.get($scope.server_url + "/stats", {}).then(
                function (response) {
                    console.log("Response from server: " + JSON.stringify(response.data));
                    $scope.cont_mutantes = response.data.count_mutant_dna;
                    $scope.cont_humanos = response.data.count_human_dna;
                    $scope.ratio = response.data.ratio;
                    $scope.last_transaction_time_stats = $scope.getDate();
                },
                function (response) {
                    console.log("Error! Response from server: " + JSON.stringify(response));
                    $scope.last_transaction_time_stats = $scope.getDate();
                }
        );
    };

    function makeRandomString(len) {
        var text = "";
        var possible = "CGAT";

        for (var i = 0; i < len; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }

    $scope.setRandomDNA = function () {
        
        var n = parseInt($scope.randomSize);
        if (isNaN(n)) { 
            alert("Ingrese un número");
            return;
        }
        
        $scope.current_dna = [];

        for (var i = 0; i < n; i++) {
            $scope.current_dna[i] = makeRandomString(n);
        }
    };

    // Actualizar los datos por primera vez 
    $scope.getStats();
});
