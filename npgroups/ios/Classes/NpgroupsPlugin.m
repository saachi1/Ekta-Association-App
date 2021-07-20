#import "NpgroupsPlugin.h"
#if __has_include(<npgroups/npgroups-Swift.h>)
#import <npgroups/npgroups-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "npgroups-Swift.h"
#endif

@implementation NpgroupsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftNpgroupsPlugin registerWithRegistrar:registrar];
}
@end
