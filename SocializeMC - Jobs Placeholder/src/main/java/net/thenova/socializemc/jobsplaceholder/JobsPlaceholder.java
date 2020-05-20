package net.thenova.socializemc.jobsplaceholder;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class JobsPlaceholder extends PlaceholderExpansion {

    private Jobs pl;

    public boolean register() {
        this.pl = (Jobs) Bukkit.getPluginManager().getPlugin("Jobs");
        if (this.pl == null) {
            return false;
        }
        return super.register();
    }

    @Override
    public String getIdentifier() {
        return "socializemc";
    }

    @Override
    public String getAuthor() {
        return "ipr0james";
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        switch (identifier) {
            case "job": {
                final JobsPlayer jobsPlayer;
                if((jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player)) == null) {
                    return "None";
                }

                final List<JobProgression> jobs = jobsPlayer.getJobProgression();

                if(jobs.isEmpty()) {
                    return "None";
                } else {
                    try {
                        return jobs.get(0).getJob().getName();
                    } catch (IndexOutOfBoundsException ex) {
                        return "None";
                    }
                }
            }
        }
        return "";
    }
}

