package com.example.agendacontato;

import android.graphics.Color;

/**
 * Helper class for generating user avatars with initials and colors
 */
public class AvatarHelper {
    
    // Color palette for avatars
    private static final int[] AVATAR_COLORS = {
        0xFFFF6B6B, // Red
        0xFF4ECDC4, // Teal
        0xFF45B7D1, // Sky Blue
        0xFFFFA07A, // Light Salmon
        0xFF98D8C8, // Turquoise
        0xFFF7DC6F, // Yellow
        0xFFBB8FCE, // Purple
        0xFF85C1E2  // Light Blue
    };

    /**
     * Get initials from a name (first letter of first and last name)
     * @param name Full name
     * @return Initials (max 2 characters)
     */
    public static String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }

        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        } else {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
    }

    /**
     * Generate a consistent background color for a name
     * @param name Full name
     * @return Color int
     */
    public static int getColorForName(String name) {
        if (name == null || name.isEmpty()) {
            return AVATAR_COLORS[0];
        }

        // Use hashCode to get consistent color for same name
        int hash = Math.abs(name.hashCode());
        int index = hash % AVATAR_COLORS.length;
        return AVATAR_COLORS[index];
    }

    /**
     * Check if a color is light or dark (for determining text color)
     * @param color Color int
     * @return true if light, false if dark
     */
    public static boolean isColorLight(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        
        // Calculate luminance
        double luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255;
        return luminance > 0.5;
    }
}
