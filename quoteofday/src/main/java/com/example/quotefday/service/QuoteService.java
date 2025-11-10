package com.example.quotefday.service;

import com.example.quotefday.model.Quote;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GenerationConfig;
import com.google.genai.types.GoogleSearch;
import com.google.genai.types.HarmBlockThreshold;
import com.google.genai.types.HarmCategory;
import com.google.genai.types.Part;
import com.google.genai.types.SafetySetting;
import com.google.genai.types.Tool;


@Service
public class QuoteService {

    private static String quoteDirectory = "/quotes/";
    private static String apiKey = "mysecret-api-1231523234";

    private void testClientConnection() {
        Client client = Client.builder().apiKey("YOUR_API_KEY").build();
        String systemPrompt = "Generate an inspirational quote about the following topic: " + "german cars";
        Content systemInstruction = Content.fromParts(Part.fromText(systemPrompt));
        Tool googleSearchTool = Tool.builder().googleSearch(GoogleSearch.builder().build()).build();

        GenerateContentConfig config = GenerateContentConfig.builder()
            .candidateCount(1)
              .maxOutputTokens(1024)
              .systemInstruction(systemInstruction)
              .tools(List.of(googleSearchTool))
              .build();

        GenerateContentResponse response = client.models
        .generateContent("gemini-2.0-flash-001", "test", config);

        String aiGeneratedQuote = response.text();

    }

    private final List<Quote> quotes = Arrays.asList(
            new Quote("The only way to do great work is to love whats you do.", "Steve Jobs", LocalDate.now()),
            new Quote("Life is what happens when you're busy making other plans.", "John Lennon", LocalDate.now()),
            new Quote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt", LocalDate.now()),
            new Quote("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill", LocalDate.now()),
            new Quote("The only limit to our realization of tomorrow is our doubts of today.", "Franklin D. Roosevelt", LocalDate.now()),
            new Quote("It does not matter how slowly you go as long as you do not stop.", "Confucius", LocalDate.now()),
            new Quote("The journey of a thousand miles begins with one step.", "Lao Tzu", LocalDate.now()),
            new Quote("What you get by achieving your goals is not as important as what you become by achieving your goals.", "Zig Ziglar", LocalDate.now()),
            new Quote("The best way to predict the future is to invent it.", "Alan Kay", LocalDate.now()),
            new Quote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson", LocalDate.now()),
            new Quote("Believe you can and you're halfway there.", "Theodore Roosevelt", LocalDate.now()),
            new Quote("The mind is everything. What you think you become.", "Buddha", LocalDate.now()),
            new Quote("The only person you are destined to become is the person you decide to be.", "Ralph Waldo Emerson", LocalDate.now()),
            new Quote("Everything you've ever wanted is on the other side of fear.", "George Addair", LocalDate.now()),
            new Quote("The way to get started is to quit talking and begin doing.", "Walt Disney", LocalDate.now()),
            new Quote("Your time is limited, don't waste it living someone else's life.", "Steve Jobs", LocalDate.now()),
            new Quote("The greatest glory in living lies not in never falling, but in rising every time we fall.", "Nelson Mandela", LocalDate.now()),
            new Quote("In the middle of difficulty lies opportunity.", "Albert Einstein", LocalDate.now()),
            new Quote("The only impossible journey is the one you never begin.", "Tony Robbins", LocalDate.now()),
            new Quote("What you do today can improve all your tomorrows.", "Ralph Marston", LocalDate.now()),
            new Quote("The secret of getting ahead is getting started.", "Mark Twain", LocalDate.now()),
            new Quote("Don't let yesterday take up too much of today.", "Will Rogers", LocalDate.now()),
            new Quote("The harder you work for something, the greater you'll feel when you achieve it.", "Unknown", LocalDate.now()),
            new Quote("Dream big and dare to fail.", "Norman Vaughan", LocalDate.now()),
            new Quote("The best revenge is massive success.", "Frank Sinatra", LocalDate.now()),
            new Quote("I find that the harder I work, the more luck I seem to have.", "Thomas Jefferson", LocalDate.now()),
            new Quote("Success is walking from failure to failure with no loss of enthusiasm.", "Winston Churchill", LocalDate.now()),
            new Quote("The only place where success comes before work is in the dictionary.", "Vidal Sassoon", LocalDate.now()),
            new Quote("Don't be afraid to give up the good to go for the great.", "John D. Rockefeller", LocalDate.now()),
            new Quote("The future depends on what you do today.", "Mahatma Gandhi", LocalDate.now())
    );

    public Quote getQuoteOfTheDay() {
        int dayOfYear = LocalDate.now().getDayOfYear();
        int index = dayOfYear % quotes.size();
        Quote quote = quotes.get(index);
        quote.setDate(LocalDate.now());
        return quote;
    }

    public Quote getQuoteOfTheDay(LocalDate date) {
        int dayOfYear = date.getDayOfYear();
        int index = dayOfYear % quotes.size();
        Quote quote = quotes.get(index);
        quote.setDate(date);
        return quote;
    }

    public Quote getQuoteByAuthor(String name) {

        Quote quote = new Quote();
        try {
            FileInputStream inputStream = new FileInputStream(quoteDirectory + name);
            quote.setText(IOUtils.toString(inputStream));
        } catch(java.io.FileNotFoundException e) {
            quote.setText("File not found");
        } catch(java.io.IOException e) {
            quote.setText("Error opening file");
        } catch(Exception e) {
            quote.setText("Error");
        }
        return quote;
    }

    public Quote getQuoteFromAI(String topic) {
        
        Quote quote = new Quote();
        // Create new client and generate content with system prompt for a specific topic
        Client client = Client.builder().apiKey(apiKey).build();
        String systemPrompt = "Generate an inspirational quote about the following topic: " + topic;
        Content systemInstruction = Content.fromParts(Part.fromText(systemPrompt));
        GenerateContentConfig config = GenerateContentConfig.builder()
            .candidateCount(1)
              .maxOutputTokens(1024)
              .systemInstruction(systemInstruction)
              .build();
        GenerateContentResponse response = client.models
        .generateContent("gemini-2.0-flash-001", "quote-generation", config);
        String aiGeneratedQuote = response.text();
        quote.setText(aiGeneratedQuote);
        quote.setAuthor("AI Generated");
        quote.setDate(LocalDate.now());



        
        return quote;
    }

    public List<Quote> getAllQuotes() {
        return quotes.stream()
                .map(quote -> new Quote(quote.getText(), quote.getAuthor(), LocalDate.now()))
                .toList();
    }
}