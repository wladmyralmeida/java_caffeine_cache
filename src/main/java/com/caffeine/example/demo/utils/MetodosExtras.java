package com.caffeine.example.demo.utils;

import com.caffeine.example.demo.data.DataObject;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class MetodosExtras {

    // método para atualizar as entradas após um período definido automagicamente;
    public static void refreshAfterWhite(){
        Caffeine.newBuilder()
                .refreshAfterWrite(1, TimeUnit.MINUTES)
                .build(k -> DataObject.get("Data for " + k));

        //A diferença entre o expiresAfter e o refresh é que, quando é a entrada expirada é solicitada
        //uma execução é bloqueada até que o novo valor seja calculado pela função Build;

        //Enquanto que se for a entrada for para atualização, o cache retornará um valor antigo e recarregará
        //o valor de forma assíncrona;
    }

    // método para registrar as estatísticas de uso;
    public static void registerCacheUse(){
        LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .recordStats()
                .build(k -> DataObject.get("Data for " + k));
        cache.get("A");
        cache.get("A");

        assertEquals(1, cache.stats().hitCount());
        assertEquals(1, cache.stats().missCount());

        // O recordStats, cria uma implementação do StatsCounter.
        // Este objeto será enviado com todas as alterações relacionadas às estatísticas.
    }
}
