package com.caffeine.example.demo.utils;

import com.caffeine.example.demo.data.DataObject;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MetodosParaInicializarCache {

    public static void initializeCaffeineManually() {
        //Inicializar o cache manualmente setando sua expiração em 1 min;
        Cache<String, DataObject> cache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();

        String key = "A";
        // obter valor;
        DataObject object = cache.getIfPresent(key);

        // obter valor e preencher com um valor substituto se a chave não existir;
        // recebe uma função como arg que só será executada uma fez independente de solicitações futuras;
        object = cache.get(key, k -> DataObject.get("Data para substituir 'A'"));

        // inserir algum valor;
        cache.put(key, object);

        //invalidar valores em cache manualmente
        cache.invalidate(key);

        // possíveis comparações para saídas do método
        assertNull(object);
        assertNotNull(object);
        assertEquals("Data for A", object.getData());
    }

    public static void initializeCaffeineSync() {
        String key = "B";

        // recebe uma função que é usada para inicializar valores, como o get, facilitando sua recuperação;
        LoadingCache<String, DataObject> syncLoadingCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(k -> DataObject.get("Data for " + k));

        // facilidade aplicada;
        DataObject object = syncLoadingCache.get(key);

        // mapear e recuperar um conjunto de valores;
        Map<String, DataObject> dataObjectMap
                = syncLoadingCache.getAll(Arrays.asList("A", "B", "C"));

        // verificar tamanho do map;
        assertEquals(3, dataObjectMap.size());

        /* Observação:
             Os valores são recuperados da Função de inicialização de back-end subjacente que foi passada
             para o método de construção.
             !! Isso torna possível usar o cache como a fachada principal para acessar os valores.
        */
    }

    // funciona da mesma forma que a sync, mas executa operações de forma assíncrona e retorna um CompletableFuture.
    public static void initializeCaffeineAsync(){

        AsyncLoadingCache<String, DataObject> asyncLoadingCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .buildAsync(k -> DataObject.get("Data for " + k));

        String key = "A";

        asyncLoadingCache.get(key).thenAccept(dataObject -> {
            assertNotNull(dataObject);
            assertEquals("Data for " + key, dataObject.getData());
        });

        asyncLoadingCache.getAll(Arrays.asList("A", "B", "C"))
                .thenAccept(dataObjectMap -> assertEquals(3, dataObjectMap.size()));

        //Link to CompletableFuture Guide => https://www.baeldung.com/java-completablefuture
    }
}
