package service;

import data.Winner;
import data.WinnerRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WinnerService {

    private final WinnerRepository winnerRepository;

    /**
     * Constructs a WinnerService with the given WinnerRepository.
     *
     * @param winnerRepository the repository for managing Winner entities
     */
    public WinnerService(WinnerRepository winnerRepository) {
        this.winnerRepository = winnerRepository;
    }

    /**
     * Adds a new winner to the repository.
     *
     * @param winner the Winner to add
     */
    public void addWinner(Winner winner) {
        winnerRepository.add(winner);
    }

    /**
     * Retrieves the top 10 winners based on the least number of moves.
     *
     * @return a list of the top 10 winners sorted by moves
     */
    public List<Winner> getTopTenWinners() {
        return winnerRepository.getTopTenWinners();
    }

    /**
     * Loads winners from a file into the repository.
     *
     * @param filePath the path to the file containing winners
     */
    public void loadWinnersFromFile(File filePath) {
        try {
            winnerRepository.loadFromFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load winners from file: " + filePath, e);
        }
    }

    /**
     * Saves winners to a file.
     *
     * @param filePath the path to the file where winners will be saved
     */
    public void saveWinnersToFile(File filePath) {
        try {
            winnerRepository.saveToFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save winners to file: " + filePath, e);
        }
    }

    /**
     * Checks if the winner repository is empty.
     *
     * @return true if the repository is empty, false otherwise
     */
    public boolean isRepositoryEmpty() {
        return winnerRepository.getElements().isEmpty();
    }

    /**
     * Clears all winners from the repository.
     */
    public void clearWinners() {
        winnerRepository.clear();
    }
}

