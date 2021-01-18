package io.quarkus.registry.app.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;

/**
 * Quarkus core releases
 */
@Entity
@NamedQuery(name = "CoreRelease.findAllVersions", query = "SELECT r.version FROM CoreRelease r ORDER BY r.createdAt DESC")
@Table(indexes = { @Index(columnList = "version", unique = true) })
public class CoreRelease extends BaseEntity {

    @Column(nullable = false)
    public String version;

    @ManyToOne
    public CoreRelease majorRelease;

    @OneToMany(mappedBy = "majorRelease")
    public List<CoreRelease> minorReleases;

    @ManyToMany
    public List<ExtensionRelease> compatibleExtensions;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoreRelease)) {
            return false;
        }
        CoreRelease that = (CoreRelease) o;
        return Objects.equals(version, that.version);
    }

    @Override public String toString() {
        return "CoreRelease{" +
                "version='" + version + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }

    public boolean isPreRelease() {
        return !version.endsWith("Final");
    }

    public static Multi<String> findAllVersions(PgPool client) {
        return client.query("SELECT version FROM core_release ORDER BY created_at DESC").execute()
                // Create a Multi from the set of rows:
                .onItem()
                .transformToMulti(set -> Multi.createFrom().items(() -> StreamSupport.stream(set.spliterator(), false)))
                // For each row return the first column
                .onItem().transform(row -> row.get(String.class, 0));
    }
}