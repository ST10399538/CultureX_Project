namespace CultureXAPI.DTOs
{
    public class UserSettingsDTO
    {

        public string PreferredLanguage { get; set; }
        public bool BiometricEnabled { get; set; }
        public object? NotificationPreferences { get; set; }

    }
}
