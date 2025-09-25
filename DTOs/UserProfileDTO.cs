namespace CultureXAPI.DTOs
{
    public class UserProfileDTO
    {

        public Guid Id { get; set; }
        public string Email { get; set; }
        public string DisplayName { get; set; }
        public string? ProfilePictureUrl { get; set; }
        public string PreferredLanguage { get; set; }
        public bool BiometricEnabled { get; set; }
        public object? NotificationPreferences { get; set; }

    }
}
